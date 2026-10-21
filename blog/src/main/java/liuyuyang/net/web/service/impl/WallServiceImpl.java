package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
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
import liuyuyang.net.web.service.WebConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private WebConfigService configService;

    private void sendWallNotifyEmail(Wall wall) {
        WallCate cate = wallCateMapper.selectById(wall.getCateId());

        Context context = new Context();
        context.setVariable("recipient", wall.getName());
        context.setVariable("cate", cate != null ? cate.getName() : "未知");
        context.setVariable("email", wall.getEmail() != null && !wall.getEmail().isEmpty() ? wall.getEmail() : "未填写");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        context.setVariable("time", now.format(formatter));
        context.setVariable("content", wall.getContent());

        String siteUrl = (String) configService.getByName("web").getValue().get("url");
        context.setVariable("url", String.format("%s/wall", siteUrl));

        String template = templateEngine.process("wall_notify_email", context);
        emailUtils.send(null, "您有新的留言等待审核", template);
    }

    private void validateSubmitCate(Integer cateId) {
        WallCate wallCate = wallCateMapper.selectById(cateId);
        if (wallCate == null) {
            throw new CustomException("留言分类不存在");
        }
        // 不让这俩类型提交留言
        String mark = wallCate.getMark();
        if (Objects.equals(mark, "all") || Objects.equals(mark, "choice")) {
            throw new CustomException("该分类不允许提交留言");
        }
    }

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
        validateSubmitCate(wallFormDTO.getCateId());

        Wall wall = new Wall();
        BeanUtils.copyProperties(wallFormDTO, wall);
        // 匿名提交一律待审核且非精选，请求体中的 status/isChoice 不生效
        wall.setStatus(WallAuditStatusEnum.PENDING);
        wall.setIsChoice(0);
        wallMapper.insert(wall);
        sendWallNotifyEmail(wall);
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

    private QueryWrapper<Wall> buildWallQueryWrapper(WallFilterDTO wallFilterDTO) {
        QueryWrapper<Wall> queryWrapper = commonUtils.queryWrapperDateFilter(wallFilterDTO);
        // 非管理员强制只看已审核，防止翻看待审核内容
        WallAuditStatusEnum status = commonUtils.isAdmin() && wallFilterDTO.getStatus() != null
                ? wallFilterDTO.getStatus()
                : WallAuditStatusEnum.APPROVED;
        queryWrapper.eq("status", status.getValue());

        if (wallFilterDTO.getCateId() != null) {
            queryWrapper.eq("cate_id", wallFilterDTO.getCateId());
        }

        if (wallFilterDTO.getIsChoice() != null) {
            queryWrapper.eq("is_choice", wallFilterDTO.getIsChoice());
        }

        if (wallFilterDTO.getContent() != null && !wallFilterDTO.getContent().trim().isEmpty()) {
            queryWrapper.like("content", "%" + wallFilterDTO.getContent().trim() + "%");
        }

        return queryWrapper;
    }

    // 批量回填当页留言的分类，替代逐条 selectById 的 N+1 查询
    private void fillWallCate(List<Wall> walls) {
        if (walls == null || walls.isEmpty()) {
            return;
        }
        List<Integer> cateIds = walls.stream()
                .map(Wall::getCateId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (cateIds.isEmpty()) {
            return;
        }
        Map<Integer, WallCate> cateMap = wallCateMapper.selectByIds(cateIds).stream()
                .collect(Collectors.toMap(WallCate::getId, c -> c));
        walls.forEach(w -> w.setCate(cateMap.get(w.getCateId())));
    }

    @Override
    public Page<WallVO> getWallList(WallFilterDTO wallFilterDTO) {
        QueryWrapper<Wall> queryWrapper = buildWallQueryWrapper(wallFilterDTO);

        Page<Wall> page = CommonUtils.buildPage(wallFilterDTO);
        List<Wall> raw;
        Page<WallVO> result;
        if (page == null) {
            raw = wallMapper.selectList(queryWrapper);
            result = new Page<>(1, raw.size());
            result.setTotal(raw.size());
        } else {
            Page<Wall> entityPage = wallMapper.selectPage(page, queryWrapper);
            raw = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }

        fillWallCate(raw);
        List<WallVO> list = raw.stream().map(WallServiceImpl::toWallVO).collect(Collectors.toList());
        result.setRecords(list);
        return result;
    }

    @Override
    public Page<WallVO> getCateWallList(Integer cateId, PageDTO pageDTO) {
        WallCate wallCate = wallCateMapper.selectById(cateId);
        if (wallCate == null) {
            throw new CustomException("该留言分类不存在");
        }

        QueryWrapper<Wall> queryWrapper = getWallQueryWrapper(cateId, wallCate);

        Page<Wall> page = CommonUtils.buildPage(pageDTO);
        List<Wall> raw;
        Page<WallVO> result;
        if (page == null) {
            raw = wallMapper.selectList(queryWrapper);
            result = new Page<>(1, raw.size());
            result.setTotal(raw.size());
        } else {
            Page<Wall> entityPage = wallMapper.selectPage(page, queryWrapper);
            raw = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }

        fillWallCate(raw);
        List<WallVO> vos = raw.stream().map(WallServiceImpl::toWallVO).collect(Collectors.toList());
        result.setRecords(vos);
        return result;
    }

    private static QueryWrapper<Wall> getWallQueryWrapper(Integer cateId, WallCate wallCate) {
        QueryWrapper<Wall> queryWrapper = new QueryWrapper<>();

        // 如果传递不是all全部，则根据分类标识过滤
        if (!Objects.equals(wallCate.getMark(), "all")) {
            // 如果分类标识是choice，则过滤精选留言
            if (Objects.equals(wallCate.getMark(), "choice")) {
                queryWrapper.eq("is_choice", 1);
            } else {
                // 否则根据分类ID过滤
                queryWrapper.eq("cate_id", cateId);
            }
        }

        // 并且是审核通过的留言
        queryWrapper.eq("status", WallAuditStatusEnum.APPROVED.getValue());

        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    @Override
    public List<WallCate> getWallCateList() {
        LambdaQueryWrapper<WallCate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(WallCate::getOrder);
        return wallCateMapper.selectList(queryWrapper);
    }

    @Override
    public void auditWallData(Integer id) {
        Wall data = wallMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该留言不存在");
        }
        data.setStatus(WallAuditStatusEnum.APPROVED);
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
