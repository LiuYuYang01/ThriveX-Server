package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.vo.seo.SeoArticleCheckVO;
import liuyuyang.net.vo.seo.SeoLinkCheckVO;
import liuyuyang.net.vo.seo.SeoSitemapCheckVO;
import liuyuyang.net.web.service.SeoService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Tag(name = "SEO 体检")
@RestController
@RequestMapping("/seo")
public class SeoController {
    @Resource
    private SeoService seoService;

    @RateLimit
    @GetMapping("/article_check")
    @Operation(summary = "检查文章缺失描述/封面")
    public Result<SeoArticleCheckVO> checkArticleMeta() {
        return Result.success(seoService.checkArticleMeta());
    }

    @RateLimit
    @GetMapping("/sitemap_check")
    @Operation(summary = "检查 sitemap 生成情况")
    public Result<SeoSitemapCheckVO> checkSitemap() {
        return Result.success(seoService.checkSitemap());
    }

    @RateLimit
    @PostMapping("/link_check")
    @Operation(summary = "检测正文死链（耗时较长，请耐心等待）")
    public Result<SeoLinkCheckVO> checkLinks() {
        return Result.success(seoService.checkLinks());
    }
}
