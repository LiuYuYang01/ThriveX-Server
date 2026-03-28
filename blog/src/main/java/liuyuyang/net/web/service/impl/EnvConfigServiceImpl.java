package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.mapper.EnvConfigMapper;
import liuyuyang.net.web.service.EnvConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EnvConfigServiceImpl extends ServiceImpl<EnvConfigMapper, EnvConfig> implements EnvConfigService {

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
} 