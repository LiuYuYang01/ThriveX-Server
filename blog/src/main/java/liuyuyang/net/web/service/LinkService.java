package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Link;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.link.LinkFilterVo;

import java.util.List;

public interface LinkService extends IService<Link> {
    void add(Link link, String token) throws Exception;

    Link get(Integer cid);

    List<Link> list(LinkFilterVo filterVo);

    Page<Link> paging(LinkFilterVo filterVo, PageVo pageVo);

    Link fetchWebsiteInfo(String url) throws Exception;
}
