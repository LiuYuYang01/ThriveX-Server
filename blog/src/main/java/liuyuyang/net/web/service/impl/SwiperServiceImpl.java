package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.swiper.SwiperFilterDTO;
import liuyuyang.net.dto.swiper.SwiperFormDTO;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.vo.swiper.SwiperVO;
import liuyuyang.net.web.mapper.SwiperMapper;
import liuyuyang.net.web.service.SwiperService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SwiperServiceImpl extends ServiceImpl<SwiperMapper, Swiper> implements SwiperService {
    @Resource
    private SwiperMapper swiperMapper;

    @Override
    public void addSwiperData(SwiperFormDTO swiperFormDTO) {
        Swiper swiper = new Swiper();
        BeanUtils.copyProperties(swiperFormDTO, swiper);
        if (swiper.getOrder() == null) {
            swiper.setOrder(nextSwiperOrder());
        }
        this.save(swiper);
    }

    @Override
    public void delSwiperData(Integer id) {
        int affected = swiperMapper.deleteById(id);
        if (affected == 0) {
            throw new CustomException("该轮播图不存在");
        }
    }

    @Override
    public void batchDelSwiperData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomException("请提供要删除的轮播图 ID");
        }
        long existCount = count(new LambdaQueryWrapper<Swiper>().in(Swiper::getId, ids));
        if (existCount != ids.size()) {
            throw new CustomException("有 " + (ids.size() - (int) existCount) + " 条轮播图不存在");
        }
        removeByIds(ids);
    }

    @Override
    public void editSwiperData(SwiperFormDTO swiperFormDTO) {
        Swiper swiper = new Swiper();
        BeanUtils.copyProperties(swiperFormDTO, swiper);
        this.updateById(swiper);
    }

    @Override
    public SwiperVO getSwiperData(Integer id) {
        Swiper swiper = swiperMapper.selectById(id);
        if (swiper == null) {
            throw new CustomException("该轮播图不存在");
        }
        SwiperVO vo = new SwiperVO();
        BeanUtils.copyProperties(swiper, vo);
        return vo;
    }

    @Override
    public Page<SwiperVO> getSwiperList(SwiperFilterDTO swiperFilterDTO) {
        LambdaQueryWrapper<Swiper> queryWrapper = new LambdaQueryWrapper<Swiper>()
                .orderByAsc(Swiper::getOrder)
                .orderByDesc(Swiper::getId);

        // 不传分页参数时返回全部（page/size 任意一个未传则全量）
        if (swiperFilterDTO == null || swiperFilterDTO.getPageNum() == null || swiperFilterDTO.getPageSize() == null) {
            List<Swiper> data = this.list(queryWrapper);
            Page<SwiperVO> result = new Page<>(1, data.size());
            // 设置数据data.stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new))
            result.setRecords(data.stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
            result.setTotal((long) data.size());
            return result;
        }

        if (swiperFilterDTO.getPageNum() <= 0 || swiperFilterDTO.getPageSize() <= 0) {
            throw new CustomException("分页参数 page/size 必须大于 0");
        }

        Page<Swiper> page = new Page<>(swiperFilterDTO.getPageNum(), swiperFilterDTO.getPageSize());
        swiperMapper.selectPage(page, queryWrapper);
        Page<SwiperVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
        return voPage;
    }

    @Override
    public void sortSwiperData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomException("请提供排序后的轮播图 ID 列表");
        }
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new CustomException("轮播图 ID 不能重复");
        }

        long existCount = count(new LambdaQueryWrapper<Swiper>().in(Swiper::getId, ids));
        if (existCount != ids.size()) {
            throw new CustomException("有 " + (ids.size() - (int) existCount) + " 条轮播图不存在");
        }

        long totalCount = count();
        if (totalCount != ids.size()) {
            throw new CustomException("请提交全部轮播图的排序结果");
        }

        for (int i = 0; i < ids.size(); i++) {
            Swiper swiper = new Swiper();
            swiper.setId(ids.get(i));
            swiper.setOrder(i + 1);
            updateById(swiper);
        }
    }

    private int nextSwiperOrder() {
        Swiper last = getOne(new LambdaQueryWrapper<Swiper>()
                .select(Swiper::getOrder)
                .orderByDesc(Swiper::getOrder)
                .last("LIMIT 1"), false);
        return last == null || last.getOrder() == null ? 1 : last.getOrder() + 1;
    }

    private SwiperVO toVO(Swiper swiper) {
        SwiperVO vo = new SwiperVO();
        BeanUtils.copyProperties(swiper, vo);
        return vo;
    }
}
