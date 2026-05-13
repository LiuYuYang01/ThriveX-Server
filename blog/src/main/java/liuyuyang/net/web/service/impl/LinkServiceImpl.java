package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.link.LinkFilterDTO;
import liuyuyang.net.dto.link.LinkFormDTO;
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

        // 如果没有设置 order 则放在最后
        if (link.getOrder() == null) {
            // 查询当前类型下的网站数量
            LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Link::getTypeId, link.getTypeId());
            List<Link> links = linkMapper.selectList(queryWrapper);
            link.setOrder(links.size() + 1);
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

        list = list.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
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
}