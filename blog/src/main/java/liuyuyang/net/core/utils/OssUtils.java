package liuyuyang.net.core.utils;


import liuyuyang.net.model.Oss;
import lombok.Data;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;
import org.dromara.x.file.storage.core.platform.FileStorage;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author laifeng
 * @version 1.0
 * @date 2024/12/10 21:21
 */
@Data
public class OssUtils {
    private static String platform;
    public static final String DEFAULT_PLATFORM = "local";
    private static final FileStorageService fileStorageService = SpringUtils.getBean(FileStorageService.class);

    public static String getPlatform() {
        if (platform == null) platform = DEFAULT_PLATFORM;
        return platform;
    }

    public static void setPlatformToDefault(Oss oss) {
        // 获取存储平台 List
        CopyOnWriteArrayList<FileStorage> list = fileStorageService.getFileStorageList();

        FileStorageProperties.LocalPlusConfig config = new FileStorageProperties.LocalPlusConfig();
        config.setPlatform(DEFAULT_PLATFORM);
        config.setBasePath(oss.getBasePath());
        config.setDomain(oss.getDomain());
        config.setStoragePath(oss.getEndPoint());
        removeStorage(list, DEFAULT_PLATFORM);

        list.addAll(FileStorageServiceBuilder.buildLocalPlusFileStorage(Collections.singletonList(config)));
    }

    /**
     * 将华为配置信息设置到存储平台
     *
     * @param oss
     */
    public static void setHuaweiConfig(Oss oss) {
        // 获得存储平台 List
        CopyOnWriteArrayList<FileStorage> list = fileStorageService.getFileStorageList();
        FileStorageProperties.HuaweiObsConfig config = new FileStorageProperties.HuaweiObsConfig();
        config.setPlatform(oss.getPlatform());
        config.setAccessKey(oss.getAccessKey());
        config.setSecretKey(oss.getSecretKey());
        config.setEndPoint(oss.getEndPoint());
        config.setBucketName(oss.getBucketName());
        config.setDomain(oss.getDomain());
        config.setBasePath(oss.getBasePath());
        removeStorage(list, oss.getPlatform());
        list.addAll(FileStorageServiceBuilder.buildHuaweiObsFileStorage(Collections.singletonList(config), null));
    }


    /**
     * 将阿里云配置信息设置到存储平台
     */
    public static void setAliyunConfig(Oss oss) {
        CopyOnWriteArrayList<FileStorage> list = fileStorageService.getFileStorageList();
        FileStorageProperties.AliyunOssConfig config = new FileStorageProperties.AliyunOssConfig();
        config.setPlatform(oss.getPlatform());
        config.setAccessKey(oss.getAccessKey());
        config.setSecretKey(oss.getSecretKey());
        config.setEndPoint(oss.getEndPoint());
        config.setBucketName(oss.getBucketName());
        config.setDomain(oss.getDomain());
        config.setBasePath(oss.getBasePath());
        removeStorage(list, oss.getPlatform());
        list.addAll(FileStorageServiceBuilder.buildAliyunOssFileStorage(Collections.singletonList(config), null));
    }

    /**
     * 将千牛云配置信息设置到存储平台
     */
    public static void setQiniuConfig(Oss oss) {
        CopyOnWriteArrayList<FileStorage> list = fileStorageService.getFileStorageList();
        FileStorageProperties.QiniuKodoConfig config = new FileStorageProperties.QiniuKodoConfig();
        config.setPlatform(oss.getPlatform());
        config.setAccessKey(oss.getAccessKey());
        config.setSecretKey(oss.getSecretKey());
        config.setBucketName(oss.getBucketName());
        config.setDomain(oss.getDomain());
        config.setBasePath(oss.getBasePath());
        removeStorage(list, oss.getPlatform());
        list.addAll(FileStorageServiceBuilder.buildQiniuKodoFileStorage(Collections.singletonList(config), null));
    }

    /**
     * 将腾讯云配置信息设置到存储平台
     */
    public static void setTencentConfig(Oss oss) {
        CopyOnWriteArrayList<FileStorage> list = fileStorageService.getFileStorageList();
        FileStorageProperties.TencentCosConfig config = new FileStorageProperties.TencentCosConfig();
        config.setPlatform(oss.getPlatform());
        config.setSecretId(oss.getAccessKey());
        config.setSecretKey(oss.getSecretKey());
        config.setBucketName(oss.getBucketName());
        config.setRegion(oss.getEndPoint());
        config.setDomain(oss.getDomain());
        config.setBasePath(oss.getBasePath());
        removeStorage(list, oss.getPlatform());
        list.addAll(FileStorageServiceBuilder.buildTencentCosFileStorage(Collections.singletonList(config), null));
    }

    /**
     * 将minio配置信息设置到存储平台
     */
    public static void setMinioConfig(Oss oss) {
        CopyOnWriteArrayList<FileStorage> list = fileStorageService.getFileStorageList();
        FileStorageProperties.MinioConfig config = new FileStorageProperties.MinioConfig();
        config.setPlatform(oss.getPlatform());
        config.setAccessKey(oss.getAccessKey());
        config.setSecretKey(oss.getSecretKey());
        config.setEndPoint(oss.getEndPoint());
        config.setBucketName(oss.getBucketName());
        config.setDomain(oss.getDomain());
        config.setBasePath(oss.getBasePath());
        removeStorage(list, oss.getPlatform());
        list.addAll(FileStorageServiceBuilder.buildMinioFileStorage(Collections.singletonList(config), null));
    }

    // 加载指定的平台
    public static void registerPlatform(Oss oss) {
        switch (oss.getPlatform()) {
            case "local":
                setPlatformToDefault(oss);
                platform = oss.getPlatform();
                return;
            case "huawei":
                setHuaweiConfig(oss);
                platform = oss.getPlatform();
                return;
            case "aliyun":
                setAliyunConfig(oss);
                platform = oss.getPlatform();
                return;
            case "qiniu":
                setQiniuConfig(oss);
                platform = oss.getPlatform();
                return;
            case "tencent":
                setTencentConfig(oss);
                platform = oss.getPlatform();
                return;
            case "minio":
                setMinioConfig(oss);
                platform = oss.getPlatform();
                return;
        }

        throw new RuntimeException("暂不支持该平台");
    }

    /**
     * 获取存储平台
     */
    public static CopyOnWriteArrayList<FileStorage> getStorageList() {
        return fileStorageService.getFileStorageList();
    }

    /**
     * 删除旧的存储平台
     */
    public static void removeStorage(CopyOnWriteArrayList<FileStorage> list, String platform) {
        FileStorage myStorage = fileStorageService.getFileStorage(platform);
        if (myStorage != null) {
            list.remove(myStorage);
            myStorage.close(); //释放资源
        }
    }
}
