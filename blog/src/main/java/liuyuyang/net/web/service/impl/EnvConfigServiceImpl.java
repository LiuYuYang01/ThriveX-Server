package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.mapper.EnvConfigMapper;
import liuyuyang.net.web.service.EnvConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class EnvConfigServiceImpl extends ServiceImpl<EnvConfigMapper, EnvConfig> implements EnvConfigService {

    // 接口响应中敏感字段的掩码值，写入时据此还原为库中原值
    public static final String SECRET_MASK = "******";
    private static final Set<String> SECRET_FIELDS = Set.of("password", "secret_key", "access_key", "access_token", "secret");

    // 默认环境配置：name -> [valueJson, notes]，与 ThriveX.sql 保持一致
    private static final Map<String, String[]> DEFAULT_CONFIGS = new LinkedHashMap<>();

    static {
        DEFAULT_CONFIGS.put("baidu_statis", new String[]{"{\"site_id\": 17256142, \"access_token\": \"\"}", "B 百度统计：在控制端首页显示网站数据"});
        DEFAULT_CONFIGS.put("email", new String[]{"{\"host\": \"smtp.qq.com\", \"port\": 465, \"password\": \"123\", \"username\": \"xxx@qq.com\"}", "邮件发送配置"});
        DEFAULT_CONFIGS.put("gaode_map_key", new String[]{"{\"key_code\": \"\", \"security_code\": \"\"}", "高德地图配置"});
        DEFAULT_CONFIGS.put("gaode_coordinate", new String[]{"{\"key\": \"xxx\"}", "高德地图坐标配置"});
        DEFAULT_CONFIGS.put("qiniu_storage", new String[]{"{\"domain\": \"\", \"zlevel\": 1, \"root_dir\": \"static\", \"end_point\": \"\", \"access_key\": \"\", \"secret_key\": \"\", \"bucket_name\": \"\"}", "七牛云存储"});
        DEFAULT_CONFIGS.put("storage", new String[]{"{\"type\": \"local\", \"domain\": \"\"}", "文件存储方式：type 为 local/qiniu，domain 为本地存储的访问域名"});
        DEFAULT_CONFIGS.put("baidu_statis_key", new String[]{"{\"key\": \"\"}", "A 百度统计：在前端获取该配置来激活统计功能"});
        DEFAULT_CONFIGS.put("hcaptcha_key", new String[]{"{\"key\": \"\", \"secret\": \"\", \"enabled\": false}", "人机验证配置：enabled 为功能开关，key 为站点密钥（公钥），secret 为服务端校验密钥（私钥），开启后登录/评论等接口强制人机验证"});
        DEFAULT_CONFIGS.put("is_system_init", new String[]{"{\"value\": false}", "系统是否初始化"});
    }

    // 启动时自动补齐缺失的默认配置，兼容旧版本数据库升级
    @PostConstruct
    public void initDefaultConfigs() {
        try {
            Map<String, EnvConfig> existing = this.list().stream()
                    .collect(Collectors.toMap(EnvConfig::getName, c -> c));
            ObjectMapper mapper = new ObjectMapper();
            DEFAULT_CONFIGS.forEach((name, config) -> {
                if (existing.containsKey(name)) return;
                try {
                    String valueJson = config[0];
                    // 老用户（已配置七牛 AK）自动保持七牛存储，避免升级后被切到本地存储
                    if ("storage".equals(name) && hasConfiguredQiniu(existing.get("qiniu_storage"))) {
                        valueJson = "{\"type\": \"qiniu\", \"domain\": \"\"}";
                    }
                    EnvConfig envConfig = new EnvConfig();
                    envConfig.setName(name);
                    envConfig.setValue(mapper.readValue(valueJson, new TypeReference<Map<String, Object>>() {
                    }));
                    envConfig.setNotes(config[1]);
                    this.save(envConfig);
                    log.info("已自动补齐缺失的环境配置：{}", name);
                } catch (Exception e) {
                    log.error("补齐环境配置{}失败", name, e);
                }
            });
        } catch (Exception e) {
            log.error("自动补齐默认环境配置失败", e);
        }
    }

    // 判断七牛配置是否已填写过 AK（视为老用户已启用七牛存储）
    private boolean hasConfiguredQiniu(EnvConfig qiniuConfig) {
        return qiniuConfig != null && qiniuConfig.getValue() != null
                && qiniuConfig.getValue().get("access_key") instanceof String accessKey
                && !accessKey.isBlank();
    }

    @Override
    public EnvConfig getById(Integer id) {
        return super.getById(id);
    }

    @Override
    public EnvConfig getByName(String name) {
        try {
            LambdaQueryWrapper<EnvConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EnvConfig::getName, name);
            return this.getOne(wrapper);
        } catch (Exception e) {
            throw new CustomException(String.format("获取%s配置失败：%s", name, e.getMessage()));
        }
    }

    @Override
    public List<EnvConfig> list() {
        return super.list();
    }

    @Override
    public boolean updateJsonValue(Integer id, Map<String, Object> jsonValue) {
        EnvConfig envConfig = this.getById(id);
        if (envConfig == null) {
            return false;
        }
        // 前端提交整包配置时，掩码字段还原为库中原值
        Map<String, Object> stored = envConfig.getValue();
        if (stored != null) {
            jsonValue.replaceAll((key, value) ->
                    SECRET_MASK.equals(value) && stored.containsKey(key) ? stored.get(key) : value);
        }
        envConfig.setValue(jsonValue);
        return this.updateById(envConfig);
    }

    @Override
    public Object getJsonFieldValue(Integer id, String fieldName) {
        EnvConfig envConfig = this.getById(id);
        if (envConfig != null && envConfig.getValue() != null) {
            return envConfig.getValue().get(fieldName);
        }
        return null;
    }

    @Override
    public boolean updateJsonFieldValue(Integer id, String fieldName, Object value) {
        EnvConfig envConfig = this.getById(id);
        if (envConfig == null) {
            return false;
        }
        // 掩码值视为未修改，跳过写入
        if (SECRET_MASK.equals(value)) {
            return true;
        }
        Map<String, Object> jsonValue = envConfig.getValue();
        if (jsonValue == null) {
            jsonValue = new HashMap<>();
        }
        jsonValue.put(fieldName, value);
        envConfig.setValue(jsonValue);
        return this.updateById(envConfig);
    }

    @Override
    public EnvConfig maskSecrets(EnvConfig config) {
        if (config == null || config.getValue() == null) {
            return config;
        }
        Map<String, Object> masked = new LinkedHashMap<>(config.getValue());
        masked.replaceAll((key, value) ->
                SECRET_FIELDS.contains(key) && value instanceof String s && !s.isBlank() ? SECRET_MASK : value);
        config.setValue(masked);
        return config;
    }

    @Override
    public Map<String, Object> getPublicConfig() {
        // 白名单式公开：只下发本来就是给前端用的配置（key 类公钥、高德地图密钥）
        Map<String, Object> data = new HashMap<>(3);
        EnvConfig baidu = this.getByName("baidu_statis_key");
        EnvConfig hcaptcha = this.getByName("hcaptcha_key");
        EnvConfig gaodeMap = this.getByName("gaode_map_key");
        data.put("baidu_statis_key", baidu != null ? baidu.getValue() : null);
        // hcaptcha 的 secret 是服务端私钥，绝不能下发给前端，这里只暴露站点密钥 key
        // 开关关闭时不下发，前端据此不渲染验证码组件
        if (CaptchaServiceImpl.isEnabled(hcaptcha)) {
            Object sitekey = hcaptcha.getValue().get("key");
            data.put("hcaptcha_key", Map.of("key", sitekey instanceof String s ? s : ""));
        } else {
            data.put("hcaptcha_key", null);
        }
        data.put("gaode_map_key", gaodeMap != null ? gaodeMap.getValue() : null);
        return data;
    }
} 