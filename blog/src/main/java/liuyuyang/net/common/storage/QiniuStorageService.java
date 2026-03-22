package liuyuyang.net.common.storage;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 七牛对象存储封装：上传/删除、按目录平铺列表、整桶目录树、逻辑目录的创建/重命名/删除。
 * <p>
 * 对象存储无真实目录，目录由 key 前缀体现；本类通过 {@code .keep} 与控制台「文件夹」两种占位策略
 * 与列表/树接口的过滤规则配合，使前端既能展示树形结构，又不会把占位对象当成用户文件。
 */
@Service
public class QiniuStorageService {
    /** 七牛配置名称 */
    private static final String CONFIG_NAME = "qiniu_storage";
    /**
     * 目录占位对象文件名。
     *
     * 背景：
     * - 七牛是对象存储，没有真实目录；
     * - 目录只是 key 前缀，空前缀不会在列表中自然出现。
     *
     * 方案：
     * - 创建目录时上传一个空对象
     * <dir>
     * /.keep；
     * - 列表接口过滤该对象（避免前端把它当文件展示）；
     * - 树接口保留其“建目录作用”（让空目录节点可见）。
     */
    private static final String PLACEHOLDER_FILE_NAME = ".keep";

    private final EnvConfigService envConfigService;

    /**
     * 是否为七牛控制台「新建文件夹」产生的目录占位对象。
     * <p>
     * 该类对象 key 以 {@code /} 结尾，体积多为 0；{@link #listFiles} 中过滤，
     * {@link #listFileTree} 中仅用于挂目录链，不进入 files。
     */
    private boolean isDirectoryMarkerKey(String key) {
        return key != null && key.endsWith("/");
    }

    public QiniuStorageService(EnvConfigService envConfigService) {
        this.envConfigService = envConfigService;
    }

    /**
     * 上传文件到七牛云。
     * <p>
     * key 规则：{@link #normalizeDirPrefix(String) 规范化目录前缀} + UUID + 原文件扩展名，避免重名覆盖。
     */
    public String upload(String dir, MultipartFile file) throws IOException {
        QiniuConfig config = getQiniuConfig();
        String key = buildObjectKey(dir, file.getOriginalFilename());
        UploadManager uploadManager = new UploadManager(new Configuration(Region.autoRegion()));
        String token = Auth.create(config.getAccessKey(), config.getSecretKey()).uploadToken(config.getBucketName());
        Response response = uploadManager.put(file.getBytes(), key, token);
        if (!response.isOK()) {
            throw new CustomException("上传文件失败");
        }
        return buildPublicUrl(config.getDomain(), key);
    }

    /**
     * 根据完整访问 URL 解析对象 key 并删除。
     */
    public boolean deleteByUrl(String url) throws QiniuException {
        QiniuConfig config = getQiniuConfig();
        String key = extractKeyFromUrl(url, config.getDomain());
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));
        bucketManager.delete(config.getBucketName(), key);
        return true;
    }

    /**
     * 查询单个对象的元信息（stat），入参可为 URL、以 / 开头的路径或纯 key。
     */
    public Map<String, Object> getFileInfo(String filePath) throws QiniuException {
        QiniuConfig config = getQiniuConfig();
        String key = extractKeyFromUrl(filePath, config.getDomain());
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));
        FileInfo fileInfo = bucketManager.stat(config.getBucketName(), key);

        Map<String, Object> data = new HashMap<>();
        data.put("name", key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key);
        data.put("path", key);
        data.put("size", fileInfo.fsize);
        data.put("hash", fileInfo.hash);
        data.put("mimeType", fileInfo.mimeType);
        data.put("putTime", fileInfo.putTime);
        data.put("url", buildPublicUrl(config.getDomain(), key));
        return data;
    }

    /**
     * 按目录分页列出文件（平铺结构）。
     *
     * 占位逻辑：
     * - 过滤 `.keep`（应用内目录占位）；
     * - 过滤 key 以 `/` 结尾的对象（七牛控制台「新建文件夹」产生的目录占位），避免当成文件展示。
     */
    public Map<String, Object> listFiles(String dir, Integer page, Integer size) throws QiniuException {
        QiniuConfig config = getQiniuConfig();
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));
        List<FileInfo> allFiles = new ArrayList<>();
        // 按前缀列举：返回 key 以 prefix 开头的所有对象（扁平列表，marker 用于翻页）
        String prefix = normalizeDirPrefix(dir);
        String marker = null;

        do {
            FileListing listing = bucketManager.listFiles(config.getBucketName(), prefix, marker, 1000, null);
            if (listing.items != null) {
                for (FileInfo item : listing.items) {
                    if (!isPlaceholderFileKey(item.key) && !isDirectoryMarkerKey(item.key)) {
                        allFiles.add(item);
                    }
                }
            }
            marker = listing.marker;
        } while (marker != null && !marker.isEmpty());

        // 新上传优先展示（putTime 降序）
        allFiles.sort((a, b) -> Long.compare(b.putTime, a.putTime));

        int total = allFiles.size();
        int startIndex = Math.max((page - 1) * size, 0);
        int endIndex = Math.min(startIndex + size, total);

        List<Map<String, Object>> result = new ArrayList<>();
        if (startIndex < total) {
            for (FileInfo item : allFiles.subList(startIndex, endIndex)) {
                Map<String, Object> data = new HashMap<>();
                String key = item.key;
                String name = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
                // 平铺列表里 type 表示扩展名（小写），与目录树里 file 节点的 ext 字段含义一致
                String ext = "";
                int extIndex = name.lastIndexOf('.');
                if (extIndex >= 0 && extIndex < name.length() - 1) {
                    ext = name.substring(extIndex + 1).toLowerCase();
                }
                data.put("basePath", normalizeDomain(config.getDomain()) + "/");
                data.put("dir", dir);
                data.put("path", key);
                data.put("name", name);
                data.put("size", item.fsize);
                data.put("type", ext);
                data.put("date", item.putTime);
                data.put("url", buildPublicUrl(config.getDomain(), key));
                result.add(data);
            }
        }

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("result", result);
        pageData.put("size", size);
        pageData.put("page", page);
        pageData.put("pages", (total + size - 1) / size);
        pageData.put("total", total);
        return pageData;
    }

    /**
     * 返回整个存储桶的文件树结构（空前缀列举全部对象）。
     *
     * 占位逻辑（关键）：
     * 1) 先不过滤 `.keep`，否则“只有占位对象的空目录”会在树中丢失；
     * 2) 遍历 key 分段时，`.keep` 仍用于创建/补齐目录节点；
     * 3) 但 `.keep` 不写入 files，不计入 fileCount/totalSize，避免污染业务统计；
     * 4) 七牛控制台「新建文件夹」的 key（以 {@code /} 结尾）只用于补全目录节点，不写入 files、不计入统计。
     */
    public Map<String, Object> listFileTree() throws QiniuException {
        QiniuConfig config = getQiniuConfig();
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));
        String prefix = normalizeDirPrefix("");
        String marker = null;
        List<FileInfo> allFiles = new ArrayList<>();

        do {
            FileListing listing = bucketManager.listFiles(config.getBucketName(), prefix, marker, 1000, null);
            if (listing.items != null) {
                allFiles.addAll(Arrays.asList(listing.items));
            }
            marker = listing.marker;
        } while (marker != null && !marker.isEmpty());

        List<Map<String, Object>> roots = new ArrayList<>();
        Map<String, Map<String, Object>> rootIndex = new LinkedHashMap<>();
        String basePath = normalizeDomain(config.getDomain()) + "/";

        for (FileInfo fileInfo : allFiles) {
            String key = fileInfo.key;
            if (key == null || key.trim().isEmpty()) {
                continue;
            }
            // 控制台「新建文件夹」：对象 key 形如 a/b/，仅用于在树中挂出目录链，不能当作文件节点
            if (isDirectoryMarkerKey(key)) {
                String trimmed = key.substring(0, key.length() - 1);
                List<String> dirSegments = new ArrayList<>();
                for (String s : trimmed.split("/")) {
                    if (!s.isEmpty()) {
                        dirSegments.add(s);
                    }
                }
                if (dirSegments.isEmpty()) {
                    continue;
                }
                Map<String, Object> markerCurrent = rootIndex.computeIfAbsent(dirSegments.get(0), name -> {
                    Map<String, Object> node = createDirNode(name, name + "/");
                    roots.add(node);
                    return node;
                });
                for (int i = 1; i < dirSegments.size(); i++) {
                    markerCurrent = getOrCreateDirChild(markerCurrent, dirSegments.get(i));
                }
                continue;
            }
            // 普通对象：按 "/" 拆段；第一段为桶内一级目录名，最后一段为文件名，中间为子目录
            String[] segments = key.split("/");
            if (segments.length == 0) {
                continue;
            }

            Map<String, Object> current = rootIndex.computeIfAbsent(segments[0], name -> {
                Map<String, Object> node = createDirNode(name, name + "/");
                roots.add(node);
                return node;
            });
            boolean isPlaceholder = isPlaceholderFileKey(key);
            // 真实文件：自根目录起逐级累加 fileCount / totalSize；.keep 不参与统计
            if (!isPlaceholder) {
                increaseDirectoryStats(current, fileInfo.fsize);
            }

            for (int i = 1; i < segments.length - 1; i++) {
                String segment = segments[i];
                Map<String, Object> childDir = getOrCreateDirChild(current, segment);
                if (!isPlaceholder) {
                    increaseDirectoryStats(childDir, fileInfo.fsize);
                }
                current = childDir;
            }

            // dir/.keep：只保证目录节点存在，不进入 files 列表
            if (isPlaceholder) {
                continue;
            }

            // 叶子段对应一个七牛对象，挂到当前目录的 files 下
            Map<String, Object> fileNode = createFileNode(fileInfo, key, basePath);
            getFiles(current).add(fileNode);
        }

        sortTreeNodes(roots);

        Map<String, Object> data = new HashMap<>();
        data.put("basePath", basePath);
        // total 为列举到的原始对象条数（含 .keep 与控制台目录占位），与树中 files 条数不一定相等
        data.put("total", allFiles.size());
        data.put("result", roots);
        return data;
    }

    /**
     * 创建目录（逻辑目录）。
     *
     * 实现方式：
     * - 上传空字节对象：.keep；
     * - 返回 node 给前端，便于“创建成功后本地直接插入目录树”，无需立即全量刷新。
     */
    public Map<String, Object> createDirectory(String dir) throws IOException {
        String normalizedDir = normalizeDirectoryPath(dir);
        QiniuConfig config = getQiniuConfig();
        String key = normalizedDir + PLACEHOLDER_FILE_NAME;
        UploadManager uploadManager = new UploadManager(new Configuration(Region.autoRegion()));
        String token = Auth.create(config.getAccessKey(), config.getSecretKey()).uploadToken(config.getBucketName());
        Response response = uploadManager.put(new byte[0], key, token);
        if (!response.isOK()) {
            throw new CustomException("创建目录失败");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dir", normalizedDir);
        result.put("placeholder", key);
        result.put("node", createDirectoryNodeFromPath(normalizedDir));
        return result;
    }

    /**
     * 重命名目录：按前缀列举后批量 move 对象。
     */
    public Map<String, Object> renameDirectory(String fromDir, String toDir) throws QiniuException {
        String fromPrefix = normalizeDirectoryPath(fromDir);
        String toPrefix = normalizeDirectoryPath(toDir);
        if (Objects.equals(fromPrefix, toPrefix)) {
            throw new CustomException("新旧目录不能相同");
        }

        QiniuConfig config = getQiniuConfig();
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));

        // 同一 bucket 内 move：保持 key 除前缀外的后缀不变，实现整棵「子树」改名
        List<String> keys = listKeysByPrefix(bucketManager, config.getBucketName(), fromPrefix);
        int moved = 0;
        for (String oldKey : keys) {
            String newKey = toPrefix + oldKey.substring(fromPrefix.length());
            bucketManager.move(config.getBucketName(), oldKey, config.getBucketName(), newKey, true);
            moved++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("fromDir", fromPrefix);
        result.put("toDir", toPrefix);
        result.put("moved", moved);
        return result;
    }

    /**
     * 删除目录：按前缀列举后批量删除对象。
     */
    public Map<String, Object> deleteDirectory(String dir) throws QiniuException {
        String prefix = normalizeDirectoryPath(dir);
        QiniuConfig config = getQiniuConfig();
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));

        List<String> keys = listKeysByPrefix(bucketManager, config.getBucketName(), prefix);
        int deleted = 0;
        for (String key : keys) {
            bucketManager.delete(config.getBucketName(), key);
            deleted++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dir", prefix);
        result.put("deleted", deleted);
        return result;
    }

    /** 创建目录节点（用于树结构） */
    private Map<String, Object> createDirNode(String name, String path) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("type", "dir");
        node.put("name", name);
        node.put("path", path);
        node.put("children", new ArrayList<Map<String, Object>>());
        node.put("files", new ArrayList<Map<String, Object>>());
        node.put("fileCount", 0);
        node.put("totalSize", 0L);
        return node;
    }

    /**
     * 将七牛 {@link FileInfo} 转为树中的 file 节点（含 url、扩展名、父级 dir 等）。
     */
    private Map<String, Object> createFileNode(FileInfo item, String key, String basePath) {
        Map<String, Object> data = new LinkedHashMap<>();
        String name = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
        String ext = "";
        int extIndex = name.lastIndexOf('.');
        if (extIndex >= 0 && extIndex < name.length() - 1) {
            ext = name.substring(extIndex + 1).toLowerCase();
        }
        data.put("type", "file");
        data.put("date", item.putTime);
        data.put("path", key);
        data.put("basePath", basePath);
        data.put("size", item.fsize);
        data.put("name", name);
        data.put("dir", key.contains("/") ? key.substring(0, key.lastIndexOf('/')) : "");
        data.put("ext", ext);
        data.put("url", basePath + key);
        return data;
    }

    /**
     * 在父目录下按名称查找子目录节点；不存在则创建并挂到 {@code children}，保证 path 为前缀拼接规则。
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getOrCreateDirChild(Map<String, Object> parent, String childName) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
        String parentPath = String.valueOf(parent.get("path"));
        String childPath = parentPath + childName + "/";

        for (Map<String, Object> child : children) {
            if (Objects.equals(child.get("path"), childPath)) {
                return child;
            }
        }
        Map<String, Object> newChild = createDirNode(childName, childPath);
        children.add(newChild);
        return newChild;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getFiles(Map<String, Object> node) {
        return (List<Map<String, Object>>) node.get("files");
    }

    /** 累加目录统计信息（文件数、体积） */
    private void increaseDirectoryStats(Map<String, Object> node, long fileSize) {
        int fileCount = ((Number) node.get("fileCount")).intValue();
        long totalSize = ((Number) node.get("totalSize")).longValue();
        node.put("fileCount", fileCount + 1);
        node.put("totalSize", totalSize + fileSize);
    }

    /**
     * 目录按 name 字典序，文件按上传时间倒序；递归子树。
     */
    @SuppressWarnings("unchecked")
    private void sortTreeNodes(List<Map<String, Object>> nodes) {
        for (Map<String, Object> node : nodes) {
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
            List<Map<String, Object>> files = (List<Map<String, Object>>) node.get("files");
            children.sort(Comparator.comparing(o -> String.valueOf(o.get("name"))));
            files.sort(
                    (a, b) -> Long.compare(((Number) b.get("date")).longValue(), ((Number) a.get("date")).longValue()));
            sortTreeNodes(children);
        }
    }

    /** 生成上传 key：目录前缀 + 随机名 + 扩展名 */
    private String buildObjectKey(String dir, String originalFilename) {
        String ext = "";
        if (originalFilename != null) {
            int index = originalFilename.lastIndexOf('.');
            if (index >= 0) {
                ext = originalFilename.substring(index);
            }
        }
        String cleanDir = normalizeDirPrefix(dir);
        return cleanDir + UUID.randomUUID().toString().replace("-", "") + ext;
    }

    /** 规范化目录前缀：去掉前导 /，补齐尾部 / */
    private String normalizeDirPrefix(String dir) {
        String value = dir == null ? "" : dir.trim();
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        if (!value.isEmpty() && !value.endsWith("/")) {
            value = value + "/";
        }
        return value;
    }

    /** 规范化目录路径，且要求不能为空 */
    private String normalizeDirectoryPath(String dir) {
        String value = normalizeDirPrefix(dir);
        if (value.isEmpty()) {
            throw new CustomException("目录不能为空");
        }
        return value;
    }

    /**
     * 判断是否为目录占位对象。
     * 约定：key 以 `/.keep` 结尾即视为占位对象。
     */
    private boolean isPlaceholderFileKey(String key) {
        return key != null && key.endsWith("/" + PLACEHOLDER_FILE_NAME);
    }

    /** 根据完整路径快速创建目录节点（用于创建目录接口回显） */
    private Map<String, Object> createDirectoryNodeFromPath(String normalizedDir) {
        String clean = normalizedDir.endsWith("/") ? normalizedDir.substring(0, normalizedDir.length() - 1)
                : normalizedDir;
        String name = clean;
        int index = clean.lastIndexOf('/');
        if (index >= 0 && index < clean.length() - 1) {
            name = clean.substring(index + 1);
        }
        return createDirNode(name, normalizedDir);
    }

    /** 按前缀列举全部对象 key（自动翻页） */
    private List<String> listKeysByPrefix(BucketManager bucketManager, String bucket, String prefix)
            throws QiniuException {
        String marker = null;
        List<String> keys = new ArrayList<>();
        do {
            FileListing listing = bucketManager.listFiles(bucket, prefix, marker, 1000, null);
            if (listing.items != null) {
                for (FileInfo item : listing.items) {
                    keys.add(item.key);
                }
            }
            marker = listing.marker;
        } while (marker != null && !marker.isEmpty());
        return keys;
    }

    /**
     * 从 URL 或 key 中提取七牛对象 key。
     * 支持以下输入：
     * 1) 完整 URL；
     * 2) / 开头路径；
     * 3) 纯 key。
     */
    private String extractKeyFromUrl(String filePath, String domain) {
        String path = filePath == null ? "" : filePath.trim();
        if (path.isEmpty()) {
            throw new CustomException("文件路径不能为空");
        }
        String normalizedDomain = normalizeDomain(domain);
        if (path.startsWith("http://") || path.startsWith("https://")) {
            try {
                URI uri = new URI(path);
                String host = uri.getHost();
                String domainHost = new URI(normalizedDomain).getHost();
                // 与当前配置的 bucket 域名一致时，只取 path 作为 key；否则仍取 path（兼容外链）
                if (host != null && domainHost != null && host.equalsIgnoreCase(domainHost)) {
                    String p = uri.getPath();
                    return p.startsWith("/") ? p.substring(1) : p;
                }
                String p = uri.getPath();
                return p.startsWith("/") ? p.substring(1) : p;
            } catch (URISyntaxException e) {
                throw new CustomException("文件路径格式不正确");
            }
        }
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

    /** 拼接公开访问 URL */
    private String buildPublicUrl(String domain, String key) {
        return normalizeDomain(domain) + "/" + key;
    }

    /** 规范化域名：补协议、去尾 / */
    private String normalizeDomain(String domain) {
        String value = domain == null ? "" : domain.trim();
        if (value.isEmpty()) {
            throw new CustomException("qiniu_storage 配置缺少 domain");
        }
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }
        if (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    /** 从 env_config 读取并校验七牛配置 */
    private QiniuConfig getQiniuConfig() {
        EnvConfig envConfig = envConfigService.getByName(CONFIG_NAME);
        if (envConfig == null || envConfig.getValue() == null) {
            throw new CustomException("未找到 qiniu_storage 配置");
        }
        Map<String, Object> value = envConfig.getValue();

        String accessKey = readRequired(value, "access_key");
        String secretKey = readRequired(value, "secret_key");
        String bucketName = readRequired(value, "bucket_name");
        String domain = readRequired(value, "domain");
        String endPoint = readOptional(value, "end_point");

        return new QiniuConfig(accessKey, secretKey, bucketName, domain, endPoint);
    }

    /** 读取必填字段 */
    private String readRequired(Map<String, Object> config, String key) {
        String value = readOptional(config, key);
        if (value == null || value.trim().isEmpty()) {
            throw new CustomException("qiniu_storage 配置缺少字段: " + key);
        }
        return value.trim();
    }

    /** 读取可选字段 */
    private String readOptional(Map<String, Object> config, String key) {
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value);
    }

    @Data
    @AllArgsConstructor
    private static class QiniuConfig {
        /** 七牛 AK */
        private String accessKey;
        /** 七牛 SK */
        private String secretKey;
        /** 存储空间 */
        private String bucketName;
        /** 访问域名 */
        private String domain;
        /** 预留字段，当前逻辑未使用 */
        private String endPoint;
    }
}
