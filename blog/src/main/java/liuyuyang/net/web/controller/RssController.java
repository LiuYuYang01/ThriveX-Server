package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.model.Rss;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.service.RssService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.*;

@Tag(name = "订阅管理")
@RestController
@RequestMapping("/rss")
public class RssController {
    @Resource
    private RssService rssService;

    @RateLimit
    @NoTokenRequired
    @GetMapping()
    @Operation(summary = "获取订阅的网站内容")
    public Result<Map<String, Object>> getRssList(PageDTO pageDTO) {
        Page<Rss> data = rssService.getRssList(pageDTO);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
