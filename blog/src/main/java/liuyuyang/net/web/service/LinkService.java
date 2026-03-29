package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Link;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.vo.link.LinkFilterDTO;

import java.util.List;

public interface LinkService extends IService<Link> {
    void add(Link link, String token) throws Exception;

    Link get(Integer cid);

    List<Link> list(LinkFilterDTO filterVo);

    Page<Link> paging(LinkFilterDTO filterVo, PageDTO pageDTO);
}
