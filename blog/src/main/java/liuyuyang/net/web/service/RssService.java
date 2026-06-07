package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.model.Rss;
import liuyuyang.net.dto.PageDTO;

public interface RssService {
    Page<Rss> getRssList(PageDTO pageDTO);
}
