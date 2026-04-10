package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.core.utils.EmailUtils;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.wall.WallFilterDTO;
import liuyuyang.net.dto.wall.WallFormDTO;
import liuyuyang.net.enums.wall.WallAuditStatusEnum;
import liuyuyang.net.model.Wall;
import liuyuyang.net.model.WallCate;
import liuyuyang.net.vo.wall.WallVO;
import liuyuyang.net.web.mapper.WallCateMapper;
import liuyuyang.net.web.mapper.WallMapper;
import liuyuyang.net.web.service.WallService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class WallServiceImpl extends ServiceImpl<WallMapper, Wall> implements WallService {
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private WallMapper wallMapper;
    @Resource
    private WallCateMapper wallCateMapper;
    @Resource
    private EmailUtils emailUtils;

    private static WallVO toWallVO(Wall wall) {
        if (wall == null) {
            return null;
        }
        WallVO vo = new WallVO();
        BeanUtils.copyProperties(wall, vo);
        return vo;
    }

    @Override
    public void addWallData(WallFormDTO wallFormDTO) throws Exception {
        Wall wall = new Wall();
        BeanUtils.copyProperties(wallFormDTO, wall);
        if (wall.getAuditStatus() == null) {
            wall.setAuditStatus(WallAuditStatusEnum.PENDING);
        }
        wallMapper.insert(wall);
        emailUtils.send(null, "您有新的留言等待审核", "");
    }

    @Override
    public void delWallData(Integer id) {
        Wall data = wallMapper.selectById(id);
        if (data == null) {
            throw new CustomException("删除留言失败：该留言不存在");
        }
        wallMapper.deleteById(id);
    }

    @Override
    public void batchDelWallData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }

    @Override
    public void editWallData(WallFormDTO wallFormDTO) {
        Wall wall = new Wall();
        BeanUtils.copyProperties(wallFormDTO, wall);
        wallMapper.updateById(wall);
    }

    @Override
    public WallVO getWallData(Integer id) {
        Wall data = wallMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该留言不存在");
        }
        data.setCate(wallCateMapper.selectById(data.getCateId()));
        return toWallVO(data);
    }

    private List<Wall> queryWallList(WallFilterDTO wallFilterDTO) {
        QueryWrapper<Wall> queryWrapper = commonUtils.queryWrapperDateFilter(wallFilterDTO, "content");
        WallAuditStatusEnum status = wallFilterDTO.getStatus() != null
                ? wallFilterDTO.getStatus()
                : WallAuditStatusEnum.APPROVED;
        queryWrapper.eq("audit_status", status.getValue());

        if (wallFilterDTO.getCateId() != null) {
            queryWrapper.eq("cate_id", wallFilterDTO.getCateId());
        }

        List<Wall> list = wallMapper.selectList(queryWrapper);

        // 绑定数据
        for (Wall wall : list) {
            wall.setCate(wallCateMapper.selectById(wall.getCateId()));
        }

        return list;
    }

    @Override
    public Page<WallVO> getWallList(WallFilterDTO wallFilterDTO) {
        List<Wall> raw = queryWallList(wallFilterDTO);
        List<WallVO> list = raw.stream().map(WallServiceImpl::toWallVO).collect(Collectors.toList());

        // 不传 page/size 则返回全部
        if (wallFilterDTO.getPageNum() == null || wallFilterDTO.getPageSize() == null) {
            Page<WallVO> result = new Page<>(1, list.size());
            result.setRecords(new ArrayList<>(list));
            result.setTotal(list.size());
            return result;
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNum(Math.max(1, wallFilterDTO.getPageNum()));
        pageDTO.setPageSize(Math.max(1, wallFilterDTO.getPageSize()));
        return commonUtils.getPageData(pageDTO, list);
    }

    @Override
    public Page<WallVO> getCateWallList(Integer cateId, PageDTO pageDTO) {
        int p = pageDTO.getPageNum() != null ? Math.max(1, pageDTO.getPageNum()) : 1;
        int s = pageDTO.getPageSize() != null ? Math.max(1, pageDTO.getPageSize()) : 5;
        WallCate wallCate = wallCateMapper.selectById(cateId);
        if (wallCate == null) {
            throw new CustomException("该留言分类不存在");
        }

        QueryWrapper<Wall> queryWrapper = new QueryWrapper<>();
        if (!Objects.equals(wallCate.getMark(), "all")) {
            if (Objects.equals(wallCate.getMark(), "choice")) {
                queryWrapper.eq("is_choice", 1);
            } else {
                queryWrapper.eq("cate_id", cateId);
            }
        }

        queryWrapper.eq("audit_status", WallAuditStatusEnum.APPROVED.getValue());
        queryWrapper.orderByDesc("create_time");

        Page<Wall> page = new Page<>(p, s);
        wallMapper.selectPage(page, queryWrapper);

        List<Wall> list = page.getRecords();

        // 绑定数据
        for (Wall wall : list) {
            wall.setCate(wallCateMapper.selectById(wall.getCateId()));
        }

        // 分页处理
        Page<WallVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(list.stream().map(WallServiceImpl::toWallVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<WallCate> getWallCateList() {
        QueryWrapper<WallCate> queryWrapper = new QueryWrapper<>();
        return wallCateMapper.selectList(queryWrapper);
    }

    @Override
    public void auditWallData(Integer id) {
        Wall data = wallMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该留言不存在");
        }
        data.setAuditStatus(WallAuditStatusEnum.APPROVED);
        wallMapper.updateById(data);
    }

    @Override
    public void updateWallChoice(Integer id) {
        Wall wall = wallMapper.selectById(id);
        if (wall == null) {
            throw new CustomException("没有这条留言");
        }

        // 如果是精选则取消，否则设置
        if (wall.getIsChoice() == 0) {
            wall.setIsChoice(1);
        } else {
            wall.setIsChoice(0);
        }

        wallMapper.updateById(wall);
    }
}
