package liuyuyang.net.core.backup;

import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import org.springframework.stereotype.Component;

/**
 * 备份存储路由：新增备份按 {@code backup_storage.type} 每次重读选择实现，后台切换即时生效、无需重启；
 * 历史备份的下载/删除按备份记录中的 storage 字段分发，切换后旧记录仍可管理。
 */
@Component
public class BackupStorageRouter {
    public static final String CONFIG_NAME = "backup_storage";
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_QINIU = "qiniu";

    private final EnvConfigService envConfigService;
    private final LocalBackupStorage localBackupStorage;
    private final QiniuBackupStorage qiniuBackupStorage;

    public BackupStorageRouter(EnvConfigService envConfigService,
                               LocalBackupStorage localBackupStorage,
                               QiniuBackupStorage qiniuBackupStorage) {
        this.envConfigService = envConfigService;
        this.localBackupStorage = localBackupStorage;
        this.qiniuBackupStorage = qiniuBackupStorage;
    }

    // 新备份使用的存储实现
    public BackupStorage current() {
        return of(readType());
    }

    // 按备份记录的 storage 字段取对应实现，未知值回退本地
    public BackupStorage of(String type) {
        return TYPE_QINIU.equalsIgnoreCase(type) ? qiniuBackupStorage : localBackupStorage;
    }

    // 配置缺失或未标注 type 时回退本地：备份含敏感数据，未显式配置不外发
    private String readType() {
        EnvConfig envConfig = envConfigService.getByName(CONFIG_NAME);
        if (envConfig == null || envConfig.getValue() == null) {
            return TYPE_LOCAL;
        }
        Object type = envConfig.getValue().get("type");
        return type == null ? TYPE_LOCAL : String.valueOf(type);
    }
}
