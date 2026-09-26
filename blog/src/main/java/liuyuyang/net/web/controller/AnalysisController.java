package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.vo.analysis.HotArticleVO;
import liuyuyang.net.vo.analysis.HotKeywordVO;
import liuyuyang.net.vo.analysis.ViewTrendItemVO;
import liuyuyang.net.web.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@Tag(name = "数据分析管理")
@Slf4j
@RestController
@RequestMapping("/analysis")
public class AnalysisController {
    @Resource
    private AnalysisService analysisService;

    @GetMapping("/hot-articles")
    @Operation(summary = "热门文章排行（基于自建浏览日志）")
    public Result<List<HotArticleVO>> getHotArticles(
            @Parameter(description = "统计天数，默认30") @RequestParam(defaultValue = "30") Integer days,
            @Parameter(description = "返回条数，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(analysisService.getHotArticles(days, limit));
    }

    @GetMapping("/hot-keywords")
    @Operation(summary = "站内搜索热词")
    public Result<List<HotKeywordVO>> getHotKeywords(
            @Parameter(description = "统计天数，默认30") @RequestParam(defaultValue = "30") Integer days,
            @Parameter(description = "返回条数，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(analysisService.getHotKeywords(days, limit));
    }

    @GetMapping("/view-trend")
    @Operation(summary = "单篇文章按天浏览趋势")
    public Result<List<ViewTrendItemVO>> getArticleViewTrend(
            @Parameter(description = "文章ID", required = true) @RequestParam Integer articleId,
            @Parameter(description = "统计天数，默认30") @RequestParam(defaultValue = "30") Integer days) {
        return Result.success(analysisService.getArticleViewTrend(articleId, days));
    }
}
