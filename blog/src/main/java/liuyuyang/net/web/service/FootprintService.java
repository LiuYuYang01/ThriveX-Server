package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Footprint;
import liuyuyang.net.dto.FilterDTO;

import java.util.List;

public interface FootprintService extends IService<Footprint> {
    List<Footprint> list(FilterDTO filterDTO);
}
