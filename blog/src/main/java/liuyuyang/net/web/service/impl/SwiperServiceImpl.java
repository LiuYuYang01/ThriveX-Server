package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.mapper.SwiperMapper;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.web.service.SwiperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SwiperServiceImpl extends ServiceImpl<SwiperMapper, Swiper> implements SwiperService {
    @Resource
    private SwiperMapper swiperMapper;

    @Override
    public Page<Swiper> getSwiperList(PageDTO pageDTO) {
        LambdaQueryWrapper<Swiper> queryWrapper = new LambdaQueryWrapper<Swiper>().orderByDesc(Swiper::getId);

        // 不传分页参数时返回全部（page/size 任意一个未传则全量）
        if (pageDTO == null || pageDTO.getPageNum() == null || pageDTO.getPageSize() == null) {
            List<Swiper> data = this.list(queryWrapper);
            Page<Swiper> result = new Page<>(1, data.size());
            result.setRecords(new ArrayList<>(data));
            result.setTotal((long) data.size());
            return result;
        }

        if (pageDTO.getPageNum() <= 0 || pageDTO.getPageSize() <= 0) {
            throw new CustomException(400, "分页参数 page/size 必须大于 0");
        }

        // 分页查询
        Page<Swiper> result = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        swiperMapper.selectPage(result, queryWrapper);
        return result;
    }
}