package liuyuyang.net.core.storage;

import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.vo.file.FileDirCreateVO;
import liuyuyang.net.vo.file.FileDirDeleteVO;
import liuyuyang.net.vo.file.FileDirRenameVO;
import liuyuyang.net.vo.file.FileInfoVO;
import liuyuyang.net.vo.file.FileListItemVO;
import liuyuyang.net.vo.file.FileTreeFileVO;
import liuyuyang.net.vo.file.FileTreeNodeVO;
import liuyuyang.net.vo.file.FileTreeVO;
import liuyuyang.net.web.service.EnvConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * 服务器本地磁盘存储实现：文件落盘到 {@code file.dir}（application.yml 的 ./upload/），
 * 经 {@code /static/upload/**} 静态资源映射对外提供访问。
 * <p>
 * key 语义与七牛实现保持一致（{@code root_dir + 业务相对目录 + UUID + 扩展名}），
 * URL 为 {@code storage.domain + /static/upload/ + key}；目录占位同样使用 {@code .keep} 空文件，
 * 保证前端文件管理页在两种存储方式下行为一致。
 * <p>
 * 所有路径拼接必须经过 {@link #resolveSecurePath} 规范化校验，防止 {@code dir} 参数携带
 * {@code ../} 等内容造成路径穿越逃出上传根目录。
 */
@Service
public class LocalStorageService implements StorageService {
    // 本地存储配置名称
    private static final String CONFIG_NAME = "storage";
    // 与 WebConfig 的静态资源映射路径保持一致
    private static final String URL_PREFIX = "/static/upload/";
    // 与七牛实现一致的目录占位文件名
    private static final String PLACEHOLDER_FILE_NAME = ".keep";

    private final EnvConfigService envConfigService;

    // 上传根目录，从 application.yml 的 file.dir 读取
    @Value("${file.dir}")
    private String baseDir;

    public LocalStorageService(EnvConfigService envConfigService) {
        this.envConfigService = envConfigService;
    }

    @Override
    public String upload(String dir, MultipartFile file) throws IOException {
        LocalConfig config = getConfig();
        String key = buildObjectKey(combineStorageDir(config.getRootDir(), dir), file.getOriginalFilename());
        Path target = resolveSecurePath(key);
        Files.createDirectories(target.getParent());
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return buildPublicUrl(config, key);
    }

    @Override
    public boolean deleteByUrl(String url) {
        String key = extractKeyFromUrl(url);
        Path target = resolveSecurePath(key);
        try {
            Files.delete(target);
        } catch (NoSuchFileException e) {
            throw new CustomException("文件不存在或已删除");
        } catch (DirectoryNotEmptyException e) {
            throw new CustomException("路径为目录，无法按文件删除");
        } catch (IOException e) {
            throw new CustomException("删除文件失败：" + e.getMessage());
        }
        return true;
    }

    @Override
    public FileInfoVO getFileInfo(String filePath) {
        String key = extractKeyFromUrl(filePath);
        Path target = resolveSecurePath(key);
        if (!Files.isRegularFile(target)) {
            throw new CustomException("文件不存在");
        }
        LocalConfig config = getConfig();

        FileInfoVO data = new FileInfoVO();
        data.setName(target.getFileName().toString());
        data.setPath(key);
        try {
            data.setSize(Files.size(target));
            data.setPutTime(Files.getLastModifiedTime(target).toMillis());
        } catch (IOException e) {
            throw new CustomException("读取文件信息失败：" + e.getMessage());
        }
        // 本地存储无七牛 hash，留空
        data.setHash("");
        String mimeType = probeMimeType(target);
        data.setMimeType(mimeType);
        data.setUrl(buildPublicUrl(config, key));
        return data;
    }

    @Override
    public List<FileListItemVO> listFileItems(String dir) {
        LocalConfig config = getConfig();
        String prefix = combineStorageDir(config.getRootDir(), dir);
        Path target = resolveSecurePath(prefix);
        if (!Files.isDirectory(target)) {
            return new ArrayList<>();
        }

        List<Path> files;
        try (Stream<Path> stream = Files.list(target)) {
            files = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> !PLACEHOLDER_FILE_NAME.equals(p.getFileName().toString()))
                    // 新上传优先展示（修改时间降序），与七牛按 putTime 降序一致
                    .sorted(Comparator.comparingLong(LocalStorageService::mtimeOf).reversed())
                    .toList();
        } catch (IOException e) {
            throw new CustomException("读取文件列表失败：" + e.getMessage());
        }

        List<FileListItemVO> result = new ArrayList<>();
        for (Path file : files) {
            FileListItemVO data = new FileListItemVO();
            String name = file.getFileName().toString();
            // 平铺列表里 type 表示扩展名（小写），与目录树里 file 节点的 ext 字段含义一致
            String ext = "";
            int extIndex = name.lastIndexOf('.');
            if (extIndex >= 0 && extIndex < name.length() - 1) {
                ext = name.substring(extIndex + 1).toLowerCase();
            }
            data.setBasePath(buildPublicBasePath(config));
            data.setDir(dir);
            data.setPath(prefix + name);
            data.setName(name);
            data.setSize(sizeOf(file));
            data.setType(ext);
            data.setDate(mtimeOf(file));
            data.setUrl(buildPublicUrl(config, prefix + name));
            result.add(data);
        }
        return result;
    }

    /**
     * 返回整个上传根目录的文件树结构。
     * <p>
     * 目录节点直接来自真实磁盘目录；与七牛实现一致，{@code .keep} 仅用于保证空目录在树中可见，
     * 不进入 files、不计入 fileCount/totalSize。
     */
    @Override
    public FileTreeVO listFileTree() {
        LocalConfig config = getConfig();
        ensureBaseRoot(config);
        Path base = baseDirPath();
        List<Path> allFiles = new ArrayList<>();
        if (Files.isDirectory(base)) {
            try (Stream<Path> stream = Files.walk(base)) {
                stream.filter(Files::isRegularFile).forEach(allFiles::add);
            } catch (IOException e) {
                throw new CustomException("读取文件树失败：" + e.getMessage());
            }
        }

        List<FileTreeNodeVO> roots = new ArrayList<>();
        Map<String, FileTreeNodeVO> rootIndex = new LinkedHashMap<>();
        String basePath = buildPublicBasePath(config);

        for (Path file : allFiles) {
            // Windows 下 relativize 产生 \ 分隔，统一为 key 语义使用的 /
            String key = base.relativize(file).toString().replace('\\', '/');
            String[] segments = key.split("/");
            if (segments.length == 0 || key.isEmpty()) {
                continue;
            }

            FileTreeNodeVO current = rootIndex.computeIfAbsent(segments[0], name -> {
                FileTreeNodeVO node = createDirNode(name, name + "/");
                roots.add(node);
                return node;
            });
            boolean isPlaceholder = key.endsWith("/" + PLACEHOLDER_FILE_NAME);
            // 真实文件：自根目录起逐级累加 fileCount / totalSize；.keep 不参与统计
            if (!isPlaceholder) {
                increaseDirectoryStats(current, sizeOf(file));
            }

            for (int i = 1; i < segments.length - 1; i++) {
                String segment = segments[i];
                FileTreeNodeVO childDir = getOrCreateDirChild(current, segment);
                if (!isPlaceholder) {
                    increaseDirectoryStats(childDir, sizeOf(file));
                }
                current = childDir;
            }

            // dir/.keep：只保证目录节点存在，不进入 files 列表
            if (isPlaceholder) {
                continue;
            }
            current.getFiles().add(createFileNode(file, key, config));
        }

        sortTreeNodes(roots);

        FileTreeVO data = new FileTreeVO();
        data.setBasePath(basePath);
        // total 为扫描到的原始文件条数（含 .keep），与树中 files 条数不一定相等
        data.setTotal(allFiles.size());
        data.setResult(roots);
        return data;
    }

    /**
     * 创建目录：真实落盘目录 + {@code .keep} 占位文件，保证空目录在目录树中可见。
     */
    @Override
    public FileDirCreateVO createDirectory(String dir) throws IOException {
        LocalConfig config = getConfig();
        String normalizedDir = normalizeDirectoryPath(combineStorageDir(config.getRootDir(), dir));
        Path target = resolveSecurePath(normalizedDir);
        Files.createDirectories(target);
        Path keep = target.resolve(PLACEHOLDER_FILE_NAME);
        if (!Files.exists(keep)) {
            Files.createFile(keep);
        }

        FileDirCreateVO result = new FileDirCreateVO();
        result.setDir(normalizedDir);
        result.setPlaceholder(normalizedDir + PLACEHOLDER_FILE_NAME);
        result.setNode(createDirectoryNodeFromPath(normalizedDir));
        return result;
    }

    // 重命名目录：本地磁盘直接整目录 move，等价于七牛按前缀批量 move。
    @Override
    public FileDirRenameVO renameDirectory(String fromDir, String toDir) {
        LocalConfig config = getConfig();
        String fromPrefix = normalizeDirectoryPath(combineStorageDir(config.getRootDir(), fromDir));
        String toPrefix = normalizeDirectoryPath(combineStorageDir(config.getRootDir(), toDir));
        if (Objects.equals(fromPrefix, toPrefix)) {
            throw new CustomException("新旧目录不能相同");
        }
        if (toPrefix.startsWith(fromPrefix)) {
            throw new CustomException("不能将目录移动到其自身内部");
        }

        Path from = resolveSecurePath(fromPrefix);
        Path to = resolveSecurePath(toPrefix);
        if (!Files.isDirectory(from)) {
            throw new CustomException("原目录不存在");
        }

        long moved;
        try (Stream<Path> stream = Files.walk(from)) {
            moved = stream.filter(Files::isRegularFile).count();
            Files.createDirectories(to.getParent());
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CustomException("重命名目录失败：" + e.getMessage());
        }

        FileDirRenameVO result = new FileDirRenameVO();
        result.setFromDir(fromPrefix);
        result.setToDir(toPrefix);
        result.setMoved((int) moved);
        return result;
    }

    /**
     * 删除目录：目录内存在真实文件（非 {@code .keep}）则拒绝删除，与七牛实现语义一致。
     */
    @Override
    public FileDirDeleteVO deleteDirectory(String dir) {
        LocalConfig config = getConfig();
        String prefix = normalizeDirectoryPath(combineStorageDir(config.getRootDir(), dir));
        Path target = resolveSecurePath(prefix);

        FileDirDeleteVO result = new FileDirDeleteVO();
        result.setDir(prefix);
        if (!Files.isDirectory(target)) {
            // 与七牛按前缀列举为空时一致：目录不存在视为删除 0 个
            result.setDeleted(0);
            return result;
        }

        List<Path> files;
        try (Stream<Path> stream = Files.walk(target)) {
            files = stream.filter(Files::isRegularFile).toList();
        } catch (IOException e) {
            throw new CustomException("读取目录失败：" + e.getMessage());
        }
        for (Path file : files) {
            if (!PLACEHOLDER_FILE_NAME.equals(file.getFileName().toString())) {
                throw new CustomException("目录内存在文件，请先删除文件后再删除目录");
            }
        }

        // 自底向上删除目录及其内容
        try (Stream<Path> stream = Files.walk(target)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new CustomException("删除目录失败：" + e.getMessage());
                }
            });
        } catch (IOException e) {
            throw new CustomException("删除目录失败：" + e.getMessage());
        }
        result.setDeleted(files.size());
        return result;
    }

    // 路径穿越防护：规范化后必须仍位于上传根目录内，且不允许就是根目录本身
    private Path resolveSecurePath(String key) {
        Path base = baseDirPath();
        Path target = base.resolve(key == null ? "" : key).normalize();
        if (!target.startsWith(base) || base.equals(target)) {
            throw new CustomException("非法的文件路径");
        }
        return target;
    }

    /**
     * 确保上传根目录（root_dir）存在且带 {@code .keep} 占位。
     * <p>
     * 本地存储初始化时磁盘为空，文件树将没有任何节点，前端会因"无当前目录"而禁用新建/上传入口，
     * 造成死锁；拉取文件树时自动补齐根目录，保证空存储下也存在唯一的根节点（与七牛 root_dir 语义一致）。
     */
    private void ensureBaseRoot(LocalConfig config) {
        String rootDir = normalizeDirPrefix(config.getRootDir());
        try {
            Path root = rootDir.isEmpty() ? baseDirPath() : resolveSecurePath(rootDir);
            Files.createDirectories(root);
            if (!rootDir.isEmpty()) {
                Path keep = root.resolve(PLACEHOLDER_FILE_NAME);
                if (!Files.exists(keep)) {
                    Files.createFile(keep);
                }
            }
        } catch (IOException e) {
            throw new CustomException("初始化上传根目录失败：" + e.getMessage());
        }
    }

    private Path baseDirPath() {
        return Paths.get(baseDir).toAbsolutePath().normalize();
    }

    // 生成上传 key：目录前缀 + 随机名 + 扩展名（扩展名仅保留字母数字，防止异常文件名携带路径符）
    private String buildObjectKey(String dir, String originalFilename) {
        String ext = "";
        if (originalFilename != null) {
            int index = originalFilename.lastIndexOf('.');
            if (index >= 0) {
                ext = originalFilename.substring(index + 1).replaceAll("[^A-Za-z0-9]", "");
                if (ext.length() > 10) {
                    ext = ext.substring(0, 10);
                }
                if (!ext.isEmpty()) {
                    ext = "." + ext;
                }
            }
        }
        String cleanDir = normalizeDirPrefix(dir);
        return cleanDir + UUID.randomUUID().toString().replace("-", "") + ext;
    }

    /**
     * 将 {@code storage.root_dir} 与业务相对路径拼接为完整 key 前缀，拼接规则与七牛实现一致。
     */
    private String combineStorageDir(String baseDirFromConfig, String relativeDir) {
        String base = normalizeDirPrefix(baseDirFromConfig == null ? "" : baseDirFromConfig);
        String rel = normalizeDirPrefix(relativeDir == null ? "" : relativeDir);
        if (rel.isEmpty()) {
            return base;
        }
        if (base.isEmpty()) {
            return rel;
        }
        // 兼容前端传入已含 root_dir 的完整路径，避免重复前缀
        String baseSeg = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        if (!baseSeg.isEmpty() && (rel.equals(base) || rel.startsWith(baseSeg + "/"))) {
            return rel;
        }
        return base + rel;
    }

    // 规范化目录前缀：去掉前导 /，补齐尾部 /
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

    // 规范化目录路径，且要求不能为空
    private String normalizeDirectoryPath(String dir) {
        String value = normalizeDirPrefix(dir);
        if (value.isEmpty()) {
            throw new CustomException("目录不能为空");
        }
        return value;
    }

    /**
     * 从 URL 或 key 中提取对象 key。
     * 支持以下输入：
     * 1) 完整 URL（任意域名，取 path 部分）；
     * 2) 含 /static/upload/ 前缀的路径；
     * 3) 纯 key。
     */
    private String extractKeyFromUrl(String filePath) {
        String path = filePath == null ? "" : filePath.trim();
        if (path.isEmpty()) {
            throw new CustomException("文件路径不能为空");
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            try {
                path = new URI(path).getPath();
            } catch (URISyntaxException e) {
                throw new CustomException("文件路径格式不正确");
            }
        }
        int index = path.indexOf(URL_PREFIX);
        if (index >= 0) {
            return path.substring(index + URL_PREFIX.length());
        }
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String buildPublicBasePath(LocalConfig config) {
        return normalizeDomain(config.getDomain()) + URL_PREFIX;
    }

    private String buildPublicUrl(LocalConfig config, String key) {
        return buildPublicBasePath(config) + key;
    }

    // 规范化域名：补协议、去尾 /
    private String normalizeDomain(String domain) {
        String value = domain == null ? "" : domain.trim();
        if (value.isEmpty()) {
            throw new CustomException("storage 配置缺少 domain");
        }
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }
        if (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    // 从 env_config 读取并校验本地存储配置
    private LocalConfig getConfig() {
        EnvConfig envConfig = envConfigService.getByName(CONFIG_NAME);
        if (envConfig == null || envConfig.getValue() == null) {
            throw new CustomException("未找到 storage 配置");
        }
        Map<String, Object> value = envConfig.getValue();
        String domain = readString(value, "domain");
        if (domain == null || domain.trim().isEmpty()) {
            throw new CustomException("storage 配置缺少字段: domain");
        }
        String rootDir = readString(value, "root_dir");
        return new LocalConfig(rootDir == null ? "" : rootDir.trim(), domain.trim());
    }

    private String readString(Map<String, Object> config, String key) {
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private static long mtimeOf(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    private static long sizeOf(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0L;
        }
    }

    private static String probeMimeType(Path path) {
        try {
            String mimeType = Files.probeContentType(path);
            return mimeType == null ? "" : mimeType;
        } catch (IOException e) {
            return "";
        }
    }

    // 创建目录节点（用于树结构）
    private FileTreeNodeVO createDirNode(String name, String path) {
        FileTreeNodeVO node = new FileTreeNodeVO();
        node.setType("dir");
        node.setName(name);
        node.setPath(path);
        node.setChildren(new ArrayList<>());
        node.setFiles(new ArrayList<>());
        node.setFileCount(0);
        node.setTotalSize(0L);
        return node;
    }

    // 将磁盘文件转为树中的 file 节点（含 url、扩展名、父级 dir 等）；date 为修改时间的毫秒时间戳。
    private FileTreeFileVO createFileNode(Path file, String key, LocalConfig config) {
        FileTreeFileVO data = new FileTreeFileVO();
        String name = file.getFileName().toString();
        String ext = "";
        int extIndex = name.lastIndexOf('.');
        if (extIndex >= 0 && extIndex < name.length() - 1) {
            ext = name.substring(extIndex + 1).toLowerCase();
        }
        data.setType("file");
        data.setPath(key);
        data.setBasePath(buildPublicBasePath(config));
        data.setSize(sizeOf(file));
        data.setName(name);
        data.setDir(key.contains("/") ? key.substring(0, key.lastIndexOf('/')) : "");
        data.setExt(ext);
        data.setDate(mtimeOf(file));
        data.setUrl(buildPublicUrl(config, key));
        return data;
    }

    // 在父目录下按名称查找子目录节点；不存在则创建并挂到 children，保证 path 为前缀拼接规则。
    private FileTreeNodeVO getOrCreateDirChild(FileTreeNodeVO parent, String childName) {
        List<FileTreeNodeVO> children = parent.getChildren();
        String parentPath = parent.getPath();
        String childPath = parentPath + childName + "/";

        for (FileTreeNodeVO child : children) {
            if (Objects.equals(child.getPath(), childPath)) {
                return child;
            }
        }
        FileTreeNodeVO newChild = createDirNode(childName, childPath);
        children.add(newChild);
        return newChild;
    }

    // 累加目录统计信息（文件数、体积）
    private void increaseDirectoryStats(FileTreeNodeVO node, long fileSize) {
        node.setFileCount(node.getFileCount() + 1);
        node.setTotalSize(node.getTotalSize() + fileSize);
    }

    // 目录按 name 字典序，文件按上传时间倒序；递归子树。
    private void sortTreeNodes(List<FileTreeNodeVO> nodes) {
        for (FileTreeNodeVO node : nodes) {
            node.getChildren().sort(Comparator.comparing(FileTreeNodeVO::getName));
            node.getFiles().sort((a, b) -> Long.compare(b.getDate(), a.getDate()));
            sortTreeNodes(node.getChildren());
        }
    }

    // 根据完整路径快速创建目录节点（用于创建目录接口回显）
    private FileTreeNodeVO createDirectoryNodeFromPath(String normalizedDir) {
        String clean = normalizedDir.endsWith("/") ? normalizedDir.substring(0, normalizedDir.length() - 1)
                : normalizedDir;
        String name = clean;
        int index = clean.lastIndexOf('/');
        if (index >= 0 && index < clean.length() - 1) {
            name = clean.substring(index + 1);
        }
        return createDirNode(name, normalizedDir);
    }

    @Data
    @AllArgsConstructor
    private static class LocalConfig {
        // 本地存储根目录前缀
        private String rootDir;
        // 本地存储访问域名
        private String domain;
    }
}
