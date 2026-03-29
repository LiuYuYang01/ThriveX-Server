package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Wall;
import liuyuyang.net.model.WallCate;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.vo.wall.WallFilterDTO;

import java.util.List;

public interface WallService extends IService<Wall> {
    void add(Wall wall) throws Exception;

    Wall get(Integer id);

    Page<Wall> getCateWallList(Integer cateId, PageDTO pageDTO);

    List<WallCate> getCateList();

    List<Wall> list(WallFilterDTO filterVo);

    Page<Wall> paging(WallFilterDTO filterVo, PageDTO pageDTO);

    void updateChoice(Integer id);
}
