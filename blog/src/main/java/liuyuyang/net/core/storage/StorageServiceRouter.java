package liuyuyang.net.core.storage;

import jakarta.annotation.Resource;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import org.springframework.stereotype.Service;

/**
 * 存储方式路由：每次调用都读取 env_config 的 storage.type 再分发，
 * 与七牛配置的读取语义一致，后台切换存储方式即时生效、无需重启。
 */
@Service
public class StorageServiceRouter {
    public static final String CONFIG_NAME = "storage";
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_QINIU = "qiniu";

    private final EnvConfigService envConfigService;

    @Resource
    private QiniuStorageService qiniuStorageService;

    @Resource
    private LocalStorageService localStorageService;

    public StorageServiceRouter(EnvConfigService envConfigService) {
        this.envConfigService = envConfigService;
    }

    // 当前生效的存储实现
    public StorageService service() {
        return isLocal() ? localStorageService : qiniuStorageService;
    }

    public boolean isLocal() {
        return TYPE_LOCAL.equalsIgnoreCase(readType());
    }

    // 配置缺失或未标注 type 时回退七牛，保证老用户升级后行为不变
    private String readType() {
        EnvConfig envConfig = envConfigService.getByName(CONFIG_NAME);
        if (envConfig == null || envConfig.getValue() == null) {
            return TYPE_QINIU;
        }
        Object type = envConfig.getValue().get("type");
        return type == null ? TYPE_QINIU : String.valueOf(type);
    }
}
