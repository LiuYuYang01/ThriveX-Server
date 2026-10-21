package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.link.LinkFilterDTO;
import liuyuyang.net.dto.link.LinkFormDTO;
import liuyuyang.net.dto.link.LinkSortDTO;
import liuyuyang.net.enums.link.LinkStatusEnum;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.LinkType;
import liuyuyang.net.vo.link.LinkVO;
import liuyuyang.net.web.mapper.LinkMapper;
import liuyuyang.net.web.mapper.LinkTypeMapper;
import liuyuyang.net.web.service.LinkService;
import liuyuyang.net.web.service.WebConfigService;
import liuyuyang.net.core.utils.EmailUtils;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.core.utils.UrlSecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private LinkMapper linkMapper;
    @Resource
    private LinkTypeMapper linkTypeMapper;
    @Resource
    private EmailUtils emailUtils;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private WebConfigService configService;

    private void sendLinkNotifyEmail(Link link) {
        LinkType linkType = linkTypeMapper.selectById(link.getTypeId());

        Context context = new Context();
        context.setVariable("title", link.getTitle());
        context.setVariable("description", link.getDescription());
        context.setVariable("site_url", link.getUrl());
        context.setVariable("email", link.getEmail() != null && !link.getEmail().isEmpty() ? link.getEmail() : "未填写");
        context.setVariable("type", linkType != null ? linkType.getName() : "未知");
        if (link.getRss() != null && !link.getRss().isEmpty()) {
            context.setVariable("rss", link.getRss());
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        context.setVariable("time", now.format(formatter));

        String siteUrl = (String) configService.getByName("web").getValue().get("url");
        context.setVariable("url", String.format("%s/friend", siteUrl));

        String template = templateEngine.process("link_notify_email", context);
        emailUtils.send(null, "您有新的友联等待审核", template);
    }

    @Override
    public void addLinkData(LinkFormDTO linkFormDTO, String token) throws Exception {
        Link link = new Link();
        BeanUtils.copyProperties(linkFormDTO, link);
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", link.getRss());

        // 前端用户手动提交
        if (token == null || token.isEmpty()) {
            // 添加之前先判断所选的网站类型是否为当前用户可选的
            Integer isAdmin = linkTypeMapper.selectById(link.getTypeId()).getIsAdmin();
            if (isAdmin == 1)
                throw new CustomException("该类型需要管理员权限才能添加");
            // 匿名提交一律置为待审核，请求体中的 status 不生效
            link.setStatus(LinkStatusEnum.PENDING);
            linkMapper.insert(link);

            sendLinkNotifyEmail(link);
            return;
        }

        if (link.getOrder() == null || link.getOrder() == 0) {
            link.setOrder(nextLinkOrder(link.getTypeId()));
        }

        // 判断权限
        boolean isAdminPermissions = commonUtils.isAdmin();
        // 如果是超级管理员在添加时候不需要审核，直接显示
        if (isAdminPermissions) {
            link.setStatus(LinkStatusEnum.APPROVED);
            linkMapper.insert(link);
        }
    }

    @Override
    public void delLinkData(Integer id) {
        Link data = linkMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该网站不存在");
        }
        linkMapper.deleteById(id);
    }

    @Override
    public void batchDelLinkData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }

    @Override
    public void editLinkData(LinkFormDTO linkFormDTO) {
        Link link = new Link();
        BeanUtils.copyProperties(linkFormDTO, link);
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", link.getRss());
        updateById(link);
    }

    @Override
    public LinkVO getLinkData(Integer id) {
        Link data = linkMapper.selectById(id);

        if (data == null) {
            throw new CustomException("该网站不存在");
        }

        // 获取网站类型
        LinkVO linkVO = new LinkVO();
        BeanUtils.copyProperties(data, linkVO);
        linkVO.setType(linkTypeMapper.selectById(data.getTypeId()));

        return linkVO;
    }

    @Override
    public Page<LinkVO> getLinkList(LinkFilterDTO linkFilterDTO) {
        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();

        // 根据关键字通过标题过滤出对应文章数据
        if (linkFilterDTO.getTitle() != null) {
            queryWrapper.like("title", "%" + linkFilterDTO.getTitle() + "%");
        }

        if(linkFilterDTO.getStatus() != null) {
            queryWrapper.eq("status", linkFilterDTO.getStatus()); // 只显示审核成功的网站
        }

        Page<Link> page = CommonUtils.buildPage(linkFilterDTO);
        List<Link> raw;
        Page<LinkVO> result;
        if (page == null) {
            raw = linkMapper.selectList(queryWrapper);
            result = new Page<>(1, raw.size());
            result.setTotal(raw.size());
        } else {
            Page<Link> entityPage = linkMapper.selectPage(page, queryWrapper);
            raw = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }

        // 批量回填当页链接的类型，替代逐条 selectById 的 N+1 查询
        List<LinkVO> list = raw.stream().map(link -> {
            LinkVO linkVO = new LinkVO();
            BeanUtils.copyProperties(link, linkVO);
            return linkVO;
        }).collect(Collectors.toList());
        fillLinkType(list);

        // 排序：按类型 order → 网站 order → 创建时间倒序（数据量小，类型 order 跨表无法下推，内存排）
        list = list.stream()
                .sorted(Comparator
                        .comparingInt((LinkVO o) -> o.getType() == null || o.getType().getOrder() == null
                                ? Integer.MAX_VALUE
                                : o.getType().getOrder())
                        .thenComparingInt(o -> o.getOrder() == null ? Integer.MAX_VALUE : o.getOrder())
                        .thenComparing(LinkVO::getCreateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        result.setRecords(list);
        return result;
    }

    // 批量回填类型
    private void fillLinkType(List<LinkVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Integer> typeIds = list.stream()
                .map(LinkVO::getTypeId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (typeIds.isEmpty()) {
            return;
        }
        Map<Integer, LinkType> typeMap = linkTypeMapper.selectByIds(typeIds).stream()
                .collect(Collectors.toMap(LinkType::getId, t -> t));
        list.forEach(vo -> vo.setType(typeMap.get(vo.getTypeId())));
    }

    @Override
    public List<LinkType> getLinkTypeList() {
        return linkTypeMapper.selectList(null);
    }

    @Override
    public void auditLinkData(Integer id) {
        Link data = linkMapper.selectById(id);

        if (data == null) {
            throw new CustomException("该网站不存在");
        }

        data.setStatus(LinkStatusEnum.APPROVED);
        linkMapper.updateById(data);
    }

    @Override
    public void sortLinkData(LinkSortDTO linkSortDTO) {
        if (linkSortDTO == null || linkSortDTO.getTypeId() == null) {
            throw new CustomException("请提供网站类型 ID");
        }
        List<Integer> ids = linkSortDTO.getIds();
        if (ids == null || ids.isEmpty()) {
            throw new CustomException("请提供排序后的网站 ID 列表");
        }
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new CustomException("网站 ID 不能重复");
        }

        Integer typeId = linkSortDTO.getTypeId();
        long existCount = count(new LambdaQueryWrapper<Link>()
                .eq(Link::getTypeId, typeId)
                .in(Link::getId, ids));
        if (existCount != ids.size()) {
            throw new CustomException("有 " + (ids.size() - (int) existCount) + " 个网站不存在或不属于该类型");
        }

        long typeCount = count(new LambdaQueryWrapper<Link>().eq(Link::getTypeId, typeId));
        if (typeCount != ids.size()) {
            throw new CustomException("请提交该类型下全部网站的排序结果");
        }

        for (int i = 0; i < ids.size(); i++) {
            Link link = new Link();
            link.setId(ids.get(i));
            link.setOrder(i + 1);
            updateById(link);
        }
    }

    private int nextLinkOrder(Integer typeId) {
        Link last = getOne(new LambdaQueryWrapper<Link>()
                .select(Link::getOrder)
                .eq(Link::getTypeId, typeId)
                .orderByDesc(Link::getOrder)
                .last("LIMIT 1"), false);
        return last == null || last.getOrder() == null ? 1 : last.getOrder() + 1;
    }
}