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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class EnvConfigServiceImpl extends ServiceImpl<EnvConfigMapper, EnvConfig> implements EnvConfigService {

    // 默认环境配置：name -> [valueJson, notes]，与 ThriveX.sql 保持一致
    private static final Map<String, String[]> DEFAULT_CONFIGS = new LinkedHashMap<>();

    static {
        DEFAULT_CONFIGS.put("baidu_statis", new String[]{"{\"site_id\": 17256142, \"access_token\": \"\"}", "B 百度统计：在控制端首页显示网站数据"});
        DEFAULT_CONFIGS.put("email", new String[]{"{\"host\": \"smtp.qq.com\", \"port\": 465, \"password\": \"123\", \"username\": \"xxx@qq.com\"}", "邮件发送配置"});
        DEFAULT_CONFIGS.put("gaode_map_key", new String[]{"{\"key_code\": \"\", \"security_code\": \"\"}", "高德地图配置"});
        DEFAULT_CONFIGS.put("gaode_coordinate", new String[]{"{\"key\": \"xxx\"}", "高德地图坐标配置"});
        DEFAULT_CONFIGS.put("qiniu_storage", new String[]{"{\"domain\": \"\", \"zlevel\": 1, \"root_dir\": \"static\", \"end_point\": \"\", \"access_key\": \"\", \"secret_key\": \"\", \"bucket_name\": \"\"}", "七牛云存储"});
        DEFAULT_CONFIGS.put("baidu_statis_key", new String[]{"{\"key\": \"\"}", "A 百度统计：在前端获取该配置来激活统计功能"});
        DEFAULT_CONFIGS.put("hcaptcha_key", new String[]{"{\"key\": \"\"}", "人机验证配置"});
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
                    EnvConfig envConfig = new EnvConfig();
                    envConfig.setName(name);
                    envConfig.setValue(mapper.readValue(config[0], new TypeReference<Map<String, Object>>() {
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
        if (envConfig != null) {
            envConfig.setValue(jsonValue);
            return this.updateById(envConfig);
        }
        return false;
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
        if (envConfig != null) {
            Map<String, Object> jsonValue = envConfig.getValue();
            if (jsonValue == null) {
                jsonValue = new HashMap<>();
            }
            jsonValue.put(fieldName, value);
            envConfig.setValue(jsonValue);
            return this.updateById(envConfig);
        }
        return false;
    }

    @Override
    public Map<String, Object> getPublicConfig() {
        Map<String, Object> data = new HashMap<>(3);
        EnvConfig baidu = this.getByName("baidu_statis_key");
        EnvConfig hcaptcha = this.getByName("hcaptcha_key");
        EnvConfig gaodeMap = this.getByName("gaode_map_kay");
        data.put("baidu_statis_key", baidu != null ? baidu.getValue() : null);
        data.put("hcaptcha_key", hcaptcha != null ? hcaptcha.getValue() : null);
        data.put("gaode_map_kay", gaodeMap != null ? gaodeMap.getValue() : null);
        return data;
    }
} 