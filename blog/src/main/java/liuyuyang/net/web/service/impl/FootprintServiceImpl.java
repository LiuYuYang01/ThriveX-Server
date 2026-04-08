package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.footprint.FootprintFilterDTO;
import liuyuyang.net.dto.footprint.FootprintFormDTO;
import liuyuyang.net.model.Footprint;
import liuyuyang.net.vo.footprint.FootprintVO;
import liuyuyang.net.web.mapper.FootprintMapper;
import liuyuyang.net.web.service.FootprintService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FootprintServiceImpl extends ServiceImpl<FootprintMapper, Footprint> implements FootprintService {
    @Resource
    private FootprintMapper footprintMapper;
    @Resource
    private CommonUtils commonUtils;

    @Override
    public void addFootprintData(FootprintFormDTO footprintFormDTO) {
        Footprint footprint = new Footprint();
        BeanUtils.copyProperties(footprintFormDTO, footprint);
        save(footprint);
    }

    @Override
    public void delFootprintData(Integer id) {
        Footprint data = footprintMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该足迹不存在");
        }
        footprintMapper.deleteById(id);
    }

    @Override
    public void batchDelFootprintData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }

    @Override
    public void editFootprintData(FootprintFormDTO footprintFormDTO) {
        Footprint footprint = new Footprint();
        BeanUtils.copyProperties(footprintFormDTO, footprint);
        updateById(footprint);
    }

    @Override
    public FootprintVO getFootprintData(Integer id) {
        Footprint data = footprintMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该足迹不存在");
        }
        FootprintVO footprintVO = new FootprintVO();
        BeanUtils.copyProperties(data, footprintVO);
        return footprintVO;
    }

    @Override
    public Page<FootprintVO> getFootprintList(FootprintFilterDTO footprintFilterDTO) {
        QueryWrapper<Footprint> queryWrapper = commonUtils.queryWrapperDateFilter(footprintFilterDTO, "title");

        // 根据关键字通过标题过滤出对应文章数据
        if (footprintFilterDTO.getAddress() != null) {
            queryWrapper.like("address", "%" + footprintFilterDTO.getAddress() + "%");
        }

        List<FootprintVO> list = footprintMapper.selectList(queryWrapper).stream().map(item -> {
            FootprintVO footprintVO = new FootprintVO();
            BeanUtils.copyProperties(item, footprintVO);
            return footprintVO;
        }).collect(Collectors.toList());

        // 不传 page/size 则返回全部
        if (footprintFilterDTO.getPageNum() == null || footprintFilterDTO.getPageSize() == null) {
            Page<FootprintVO> result = new Page<>(1, list.size());
            result.setRecords(new ArrayList<>(list));
            result.setTotal(list.size());
            return result;
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNum(Math.max(1, footprintFilterDTO.getPageNum()));
        pageDTO.setPageSize(Math.max(1, footprintFilterDTO.getPageSize()));
        return commonUtils.getPageData(pageDTO, list);
    }
}
