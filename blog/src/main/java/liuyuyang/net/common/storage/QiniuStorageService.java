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

@Service
public class QiniuStorageService {
    private static final String CONFIG_NAME = "qiniu_storage";

    private final EnvConfigService envConfigService;

    public QiniuStorageService(EnvConfigService envConfigService) {
        this.envConfigService = envConfigService;
    }

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

    public boolean deleteByUrl(String url) throws QiniuException {
        QiniuConfig config = getQiniuConfig();
        String key = extractKeyFromUrl(url, config.getDomain());
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));
        bucketManager.delete(config.getBucketName(), key);
        return true;
    }

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

    public List<Map<String, String>> listDirectories() throws QiniuException {
        QiniuConfig config = getQiniuConfig();
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));
        Set<String> dirs = new LinkedHashSet<>();

        String marker = null;
        do {
            FileListing listing = bucketManager.listFiles(config.getBucketName(), "", marker, 1000, "/");
            if (listing.commonPrefixes != null) {
                dirs.addAll(Arrays.asList(listing.commonPrefixes));
            }
            marker = listing.marker;
        } while (marker != null && !marker.isEmpty());

        List<Map<String, String>> result = new ArrayList<>();
        for (String dir : dirs) {
            Map<String, String> item = new HashMap<>();
            item.put("name", dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir);
            item.put("path", dir);
            result.add(item);
        }
        return result;
    }

    public Map<String, Object> listFiles(String dir, Integer page, Integer size) throws QiniuException {
        QiniuConfig config = getQiniuConfig();
        BucketManager bucketManager = new BucketManager(Auth.create(config.getAccessKey(), config.getSecretKey()),
                new Configuration(Region.autoRegion()));
        List<FileInfo> allFiles = new ArrayList<>();
        String prefix = normalizeDirPrefix(dir);
        String marker = null;

        do {
            FileListing listing = bucketManager.listFiles(config.getBucketName(), prefix, marker, 1000, null);
            if (listing.items != null) {
                allFiles.addAll(Arrays.asList(listing.items));
            }
            marker = listing.marker;
        } while (marker != null && !marker.isEmpty());

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

    private String buildPublicUrl(String domain, String key) {
        return normalizeDomain(domain) + "/" + key;
    }

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

    private String readRequired(Map<String, Object> config, String key) {
        String value = readOptional(config, key);
        if (value == null || value.trim().isEmpty()) {
            throw new CustomException("qiniu_storage 配置缺少字段: " + key);
        }
        return value.trim();
    }

    private String readOptional(Map<String, Object> config, String key) {
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value);
    }

    @Data
    @AllArgsConstructor
    private static class QiniuConfig {
        private String accessKey;
        private String secretKey;
        private String bucketName;
        private String domain;
        private String endPoint;
    }
}
