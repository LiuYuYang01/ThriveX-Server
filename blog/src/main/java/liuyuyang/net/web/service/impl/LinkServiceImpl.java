package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.link.LinkFilterDTO;
import liuyuyang.net.dto.link.LinkFormDTO;
import liuyuyang.net.dto.link.LinkSortDTO;
import liuyuyang.net.enums.link.LinkStatusEnum;
import liuyuyang.net.model.Article;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.LinkType;
import liuyuyang.net.vo.link.LinkVO;
import liuyuyang.net.web.mapper.LinkMapper;
import liuyuyang.net.web.mapper.LinkTypeMapper;
import liuyuyang.net.web.service.LinkService;
import liuyuyang.net.core.utils.EmailUtils;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.core.utils.UrlSecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
            linkMapper.insert(link);

            // 邮件提醒
            emailUtils.send(null, "您有新的友联等待审核", link.toString());
            return;
        }

        if (link.getOrder() == null || link.getOrder() == 0) {
            link.setOrder(nextLinkOrder(link.getTypeId()));
        }

        // 判断权限
        boolean isAdminPermissions = CommonUtils.isAdmin();
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

        // 查询所有网站
        List<LinkVO> list = linkMapper.selectList(queryWrapper).stream().map(link -> {
            LinkVO linkVO = new LinkVO();
            BeanUtils.copyProperties(link, linkVO);
            linkVO.setType(linkTypeMapper.selectById(link.getTypeId()));
            return linkVO;
        }).collect(Collectors.toList());

        list = list.stream()
                .sorted(Comparator
                        .comparingInt((LinkVO o) -> o.getType() == null || o.getType().getOrder() == null
                                ? Integer.MAX_VALUE
                                : o.getType().getOrder())
                        .thenComparingInt(o -> o.getOrder() == null ? Integer.MAX_VALUE : o.getOrder())
                        .thenComparing(LinkVO::getCreateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // 不传 page/size 则返回全部
        if (linkFilterDTO.getPageNum() == null || linkFilterDTO.getPageSize() == null) {
            Page<LinkVO> result = new Page<>(1, list.size());
            result.setRecords(new ArrayList<>(list));
            result.setTotal(list.size());
            return result;
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNum(Math.max(1, linkFilterDTO.getPageNum()));
        pageDTO.setPageSize(Math.max(1, linkFilterDTO.getPageSize()));
        Page<LinkVO> result = commonUtils.getPageData(pageDTO, list);
        result.setTotal(list.size());
        return result;
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