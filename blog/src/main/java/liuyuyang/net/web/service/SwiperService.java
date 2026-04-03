package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.swiper.SwiperFilterDTO;
import liuyuyang.net.dto.swiper.SwiperFormDTO;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.vo.swiper.SwiperVO;

import java.util.List;

public interface SwiperService extends IService<Swiper> {
    void addSwiperData(SwiperFormDTO swiperFormDTO);

    void delSwiperData(Integer id);

    void batchDelSwiperData(List<Integer> ids);

    void editSwiperData(SwiperFormDTO swiperFormDTO);

    SwiperVO getSwiperData(Integer id);

    Page<SwiperVO> getSwiperList(SwiperFilterDTO swiperFilterDTO);
}
