package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.link.LinkFilterDTO;
import liuyuyang.net.dto.link.LinkFormDTO;
import liuyuyang.net.dto.link.LinkSortDTO;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.LinkType;
import liuyuyang.net.vo.link.LinkVO;

import java.util.List;

public interface LinkService extends IService<Link> {
    void addLinkData(LinkFormDTO linkFormDTO, String token) throws Exception;

    void delLinkData(Integer id);

    void batchDelLinkData(List<Integer> ids);

    void editLinkData(LinkFormDTO linkFormDTO);

    LinkVO getLinkData(Integer id);

    Page<LinkVO> getLinkList(LinkFilterDTO linkFilterDTO);

    List<LinkType> getLinkTypeList();

    void auditLinkData(Integer id);

    void sortLinkData(LinkSortDTO linkSortDTO);
}
