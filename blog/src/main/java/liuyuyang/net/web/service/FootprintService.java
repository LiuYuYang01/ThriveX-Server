package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.footprint.FootprintFilterDTO;
import liuyuyang.net.dto.footprint.FootprintFormDTO;
import liuyuyang.net.model.Footprint;
import liuyuyang.net.vo.footprint.FootprintVO;

import java.util.List;

public interface FootprintService extends IService<Footprint> {
    void addFootprintData(FootprintFormDTO footprintFormDTO);

    void delFootprintData(Integer id);

    void batchDelFootprintData(List<Integer> ids);

    void editFootprintData(FootprintFormDTO footprintFormDTO);

    FootprintVO getFootprintData(Integer id);

    Page<FootprintVO> getFootprintList(FootprintFilterDTO footprintFilterDTO);
}
