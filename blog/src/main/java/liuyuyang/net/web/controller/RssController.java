package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.model.Rss;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.service.RssService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Api(tags = "订阅管理")
@RestController
@RequestMapping("/rss")
public class RssController {
    @Resource
    private RssService rssService;

    @RateLimit
    @NoTokenRequired
    @GetMapping()
    @ApiOperation("获取订阅的网站内容")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<Map<String, Object>> getRssList(PageDTO pageDTO) {
        Page<Rss> data = rssService.getRssList(pageDTO);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
