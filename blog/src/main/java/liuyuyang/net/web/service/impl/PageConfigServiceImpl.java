package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.model.PageConfig;
import liuyuyang.net.web.mapper.PageConfigMapper;
import liuyuyang.net.web.service.PageConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PageConfigServiceImpl extends ServiceImpl<PageConfigMapper, PageConfig> implements PageConfigService {
    @Override
    public PageConfig getById(Integer id) {
        return super.getById(id);
    }

    @Override
    public List<PageConfig> list() {
        return super.list();
    }

    @Override
    public boolean updateJsonValue(Integer id, Map<String, Object> jsonValue) {
        PageConfig pageConfig = this.getById(id);

        if (pageConfig != null) {
            pageConfig.setValue(jsonValue);
            return this.updateById(pageConfig);
        }

        return false;
    }

    @Override
    public PageConfig getByName(String name) {
        return this.lambdaQuery().eq(PageConfig::getName, name).one();
    }
} 