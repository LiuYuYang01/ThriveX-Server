package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.vo.search.SearchVO;
import liuyuyang.net.web.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@Tag(name = "搜索管理")
@RestController
@RequestMapping("/search")
public class SearchController {
    @Resource
    private SearchService searchService;

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "统一搜索：文章按标题、闪念按内容")
    public Result<SearchVO> search(@RequestParam(defaultValue = "") String keyword,
                                   @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(searchService.search(keyword, limit));
    }
}
