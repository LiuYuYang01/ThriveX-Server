package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.wall.WallFilterDTO;
import liuyuyang.net.dto.wall.WallFormDTO;
import liuyuyang.net.model.Wall;
import liuyuyang.net.model.WallCate;
import liuyuyang.net.vo.wall.WallVO;

import java.util.List;

public interface WallService extends IService<Wall> {
    void addWallData(WallFormDTO wallFormDTO) throws Exception;

    void delWallData(Integer id);

    void batchDelWallData(List<Integer> ids);

    void editWallData(WallFormDTO wallFormDTO);

    WallVO getWallData(Integer id);

    Page<WallVO> getWallList(WallFilterDTO wallFilterDTO);

    Page<WallVO> getCateWallList(Integer cateId, PageDTO pageDTO);

    List<WallCate> getWallCateList();

    void auditWallData(Integer id);

    void updateWallChoice(Integer id);
}
