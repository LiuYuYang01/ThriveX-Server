package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.model.Footprint;
import liuyuyang.net.web.mapper.FootprintMapper;
import liuyuyang.net.web.service.FootprintService;
import liuyuyang.net.dto.FilterDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class FootprintServiceImpl extends ServiceImpl<FootprintMapper, Footprint> implements FootprintService {
    @Resource
    private FootprintMapper footprintMapper;

    @Override
    public List<Footprint> list(FilterDTO filterDTO) {
        QueryWrapper<Footprint> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        // 根据关键字通过标题过滤出对应文章数据
        if (filterDTO.getKey() != null && !filterDTO.getKey().isEmpty()) {
            queryWrapper.like("address", "%" + filterDTO.getKey() + "%");
        }

        // 根据开始与结束时间过滤
        if (filterDTO.getStartDate() != null && filterDTO.getEndDate() != null) {
            queryWrapper.between("create_time", filterDTO.getStartDate(), filterDTO.getEndDate());
        } else if (filterDTO.getStartDate() != null) {
            queryWrapper.ge("create_time", filterDTO.getStartDate());
        } else if (filterDTO.getEndDate() != null) {
            queryWrapper.le("create_time", filterDTO.getEndDate());
        }

        List<Footprint> list = footprintMapper.selectList(queryWrapper);
        return list;
    }
}