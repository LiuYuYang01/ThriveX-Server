package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.web.mapper.SwiperMapper;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.web.service.SwiperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SwiperServiceImpl extends ServiceImpl<SwiperMapper, Swiper> implements SwiperService {
    @Override
    public Page<Swiper> getSwiperList(PageVo pageVo) {
        QueryWrapper<Swiper> queryWrapper = new QueryWrapper<Swiper>().orderByDesc("id");

        // 不传分页参数时返回全部（page/size 任意一个未传则全量）
        if (pageVo == null || pageVo.getPage() == null || pageVo.getSize() == null) {
            List<Swiper> data = this.list(queryWrapper);
            Page<Swiper> result = new Page<>(1, data.size());
            result.setRecords(new ArrayList<>(data));
            result.setTotal((long) data.size());
            return result;
        }

        if (pageVo.getPage() <= 0 || pageVo.getSize() <= 0) {
            throw new CustomException(400, "分页参数非法，page/size 必须大于 0");
        }

        // 分页查询
        Page<Swiper> result = new Page<>(pageVo.getPage(), pageVo.getSize());
        baseMapper.selectPage(result, queryWrapper);
        return result;
    }
}