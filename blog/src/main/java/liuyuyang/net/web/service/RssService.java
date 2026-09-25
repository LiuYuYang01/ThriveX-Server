package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.model.Rss;
import liuyuyang.net.dto.PageDTO;

public interface RssService {
    Page<Rss> getRssList(PageDTO pageDTO);

    /**
     * 定时抓取最新内容并写回缓存（调度任务入口，声明在接口上以便被代理调用）
     */
    void refreshCache();

    /**
     * 应用启动完成后预热缓存（事件监听入口）
     */
    void prewarmCache();
}
