package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.config.CacheConfig;
import liuyuyang.net.web.mapper.WebConfigMapper;
import liuyuyang.net.model.WebConfig;
import liuyuyang.net.web.service.WebConfigService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {

    @Override
    public WebConfig getById(Integer id) {
        return super.getById(id);
    }

    @Override
    public List<WebConfig> list() {
        return super.list();
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CACHE_WEB_CONFIG, allEntries = true)
    public boolean updateJsonValue(Integer id, Map<String, Object> jsonValue) {
        WebConfig webConfig = this.getById(id);
        if (webConfig != null) {
            webConfig.setValue(jsonValue);
            return this.updateById(webConfig);
        }
        return false;
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CACHE_WEB_CONFIG, key = "#name")
    public WebConfig getByName(String name) {
        return this.lambdaQuery().eq(WebConfig::getName, name).one();
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CACHE_WEB_CONFIG, allEntries = true)
    public boolean updateJsonValueByName(String name, Map<String, Object> jsonValue) {
        WebConfig webConfig = getByName(name);
        if (webConfig == null) {
            webConfig = new WebConfig();
            webConfig.setName(name);
            webConfig.setValue(jsonValue);
            webConfig.setNotes("");
            return save(webConfig);
        }
        return updateJsonValue(webConfig.getId(), jsonValue);
    }
}