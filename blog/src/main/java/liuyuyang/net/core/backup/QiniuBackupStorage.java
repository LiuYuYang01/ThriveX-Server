package liuyuyang.net.core.backup;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

/**
 * 七牛私有空间备份存储：AK/SK 复用 {@code qiniu_storage} 配置，桶与下载域名来自 {@code backup_storage}。
 * <p>
 * 备份文件含用户表等敏感数据，桶必须设为「私有」：下载 URL 由服务端用 SK 签名（短期时效）后
 * 拉取文件流转发给浏览器，签名 URL 与密钥均不返回给前端。
 */
@Slf4j
@Component
public class QiniuBackupStorage implements BackupStorage {
    private static final String CONFIG_NAME = "backup_storage";
    private static final String QINIU_CONFIG_NAME = "qiniu_storage";
    private static final long DOWNLOAD_EXPIRE_SECONDS = 300;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final EnvConfigService envConfigService;

    public QiniuBackupStorage(EnvConfigService envConfigService) {
        this.envConfigService = envConfigService;
    }

    @Override
    public String type() {
        return "qiniu";
    }

    @Override
    public Path newTempFile(String fileName) throws IOException {
        // 远程存储无原子落位诉求，临时文件放系统临时目录；上传成功由 store 清理，失败由调用方清理
        return Files.createTempFile("thrivex-backup-", ".tmp");
    }

    @Override
    public String store(Path tempFile, String fileName) throws IOException {
        BackupConfig config = backupConfig();
        try {
            UploadManager uploadManager = new UploadManager(new Configuration(Region.autoRegion()));
            String token = config.auth().uploadToken(config.bucketName());
            Response response = uploadManager.put(tempFile.toFile(), fileName, token);
            if (!response.isOK()) {
                throw new CustomException("上传备份到七牛失败");
            }
            Files.deleteIfExists(tempFile);
            return fileName;
        } catch (QiniuException e) {
            throw new CustomException("上传备份到七牛失败：" + reasonOf(e));
        }
    }

    @Override
    public InputStream load(String key) throws IOException {
        BackupConfig config = backupConfig();
        String url = config.auth().privateDownloadUrl(normalizeDomain(config.domain()) + "/" + key, DOWNLOAD_EXPIRE_SECONDS);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30)).GET().build();
        HttpResponse<InputStream> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("下载备份被中断");
        }
        if (response.statusCode() != 200) {
            response.body().close();
            throw new CustomException("从七牛私有桶下载备份失败：HTTP " + response.statusCode());
        }
        return response.body();
    }

    @Override
    public boolean delete(String key) {
        try {
            BackupConfig config = backupConfig();
            new BucketManager(config.auth(), new Configuration(Region.autoRegion())).delete(config.bucketName(), key);
            return true;
        } catch (QiniuException e) {
            // 612：文件不存在，视为已删除
            if (e.code() != 612) {
                log.warn("删除七牛备份失败：{}", reasonOf(e));
            }
            return false;
        } catch (Exception e) {
            log.warn("删除七牛备份失败：{}", e.getMessage());
            return false;
        }
    }

    private BackupConfig backupConfig() {
        Map<String, Object> backup = readConfig(CONFIG_NAME);
        String bucketName = required(backup, "bucket_name", CONFIG_NAME);
        String domain = required(backup, "domain", CONFIG_NAME);
        Map<String, Object> qiniu = readConfig(QINIU_CONFIG_NAME);
        String accessKey = required(qiniu, "access_key", QINIU_CONFIG_NAME);
        String secretKey = required(qiniu, "secret_key", QINIU_CONFIG_NAME);
        return new BackupConfig(bucketName, domain, Auth.create(accessKey, secretKey));
    }

    private Map<String, Object> readConfig(String name) {
        EnvConfig envConfig = envConfigService.getByName(name);
        if (envConfig == null || envConfig.getValue() == null) {
            throw new CustomException("未找到 " + name + " 配置，请在后台「第三方配置」中完善");
        }
        return envConfig.getValue();
    }

    private String required(Map<String, Object> config, String key, String configName) {
        Object value = config.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new CustomException(configName + " 配置缺少字段: " + key);
        }
        return String.valueOf(value).trim();
    }

    // 规范化域名：补协议、去尾 /
    private String normalizeDomain(String domain) {
        String value = domain == null ? "" : domain.trim();
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private String reasonOf(QiniuException e) {
        return e.error() != null && !e.error().isBlank() ? e.error() : e.getMessage();
    }

    private record BackupConfig(String bucketName, String domain, Auth auth) {
    }
}
