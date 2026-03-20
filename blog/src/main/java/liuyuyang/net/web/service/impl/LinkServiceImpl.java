package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.web.mapper.LinkMapper;
import liuyuyang.net.web.mapper.LinkTypeMapper;
import liuyuyang.net.model.Link;
import liuyuyang.net.web.service.LinkService;
import liuyuyang.net.common.utils.EmailUtils;
import liuyuyang.net.common.utils.CommonUtils;
import liuyuyang.net.common.utils.UrlSecurityUtils;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.link.LinkFilterVo;
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
    public void add(Link link, String token) throws Exception {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", link.getRss());

        // 前端用户手动提交
        if (token == null || token.isEmpty()) {
            // 添加之前先判断所选的网站类型是否为当前用户可选的
            Integer isAdmin = linkTypeMapper.selectById(link.getTypeId()).getIsAdmin();
            if (isAdmin == 1) throw new CustomException(400, "该类型需要管理员权限才能添加");
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
            link.setAuditStatus(1);
            linkMapper.insert(link);
        }
    }

    @Override
    public Link get(Integer id) {
        Link data = linkMapper.selectById(id);

        if (data == null) {
            throw new CustomException(400, "该网站不存在");
        }

        // 获取网站类型
        data.setType(linkTypeMapper.selectById(id));

        return data;
    }

    @Override
    public List<Link> list(LinkFilterVo filterVo) {
        QueryWrapper<Link> queryWrapper = commonUtils.queryWrapperFilter(filterVo);
        queryWrapper.eq("audit_status", filterVo.getStatus()); // 只显示审核成功的网站

        // 查询所有网站
        List<Link> list = linkMapper.selectList(queryWrapper);

        if (!list.isEmpty()) {
            for (Link link : list) {
                link.setType(linkTypeMapper.selectById(link.getTypeId()));
            }
        }

        list = list.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).collect(Collectors.toList());

        return list;
    }

    @Override
    public Page<Link> paging(LinkFilterVo filterVo, PageVo pageVo) {
        List<Link> list = list(filterVo);
        int p = pageVo.getPage() != null ? Math.max(1, pageVo.getPage()) : 1;
        int s = pageVo.getSize() != null ? Math.max(1, pageVo.getSize()) : 5;

        // 分页处理
        int start = Math.min((p - 1) * s, list.size());
        int end = Math.min(start + s, list.size());
        List<Link> pagedLinks = start >= end ? new ArrayList<>() : list.subList(start, end);

        Page<Link> result = new Page<>(p, s);
        result.setRecords(pagedLinks);
        result.setTotal(list.size());
        return result;
    }
}