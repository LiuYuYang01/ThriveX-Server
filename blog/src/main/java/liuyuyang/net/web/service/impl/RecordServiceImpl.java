package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.web.mapper.RecordMapper;
import liuyuyang.net.model.Record;
import liuyuyang.net.web.service.RecordService;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.dto.PageDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private CommonUtils commonUtils;

    @Override
    public List<Record> list(FilterDTO filterDTO) {
        QueryWrapper<Record> queryWrapper = commonUtils.queryWrapperFilter(filterDTO, "content");
        List<Record> list = recordMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public Page<Record> paging(FilterDTO filterDTO, PageDTO pageDTO) {
        List<Record> list = list(filterDTO);
        return commonUtils.getPageData(pageDTO, list);
    }
}