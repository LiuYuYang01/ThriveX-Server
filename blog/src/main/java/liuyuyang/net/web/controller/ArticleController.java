package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.article.ArticleFormDTO;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.article.ArticleFilterDTO;
import liuyuyang.net.dto.article.ArticleLikeDTO;
import liuyuyang.net.dto.article.ArticleShareDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.article.ArticleVO;
import liuyuyang.net.web.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "文章管理")
@RestController
@RequestMapping("/article")
@Transactional
@Validated
public class ArticleController {
    @Resource
    private ArticleService articleService;

    @PostMapping
    @Operation(summary = "新增文章")
    public Result<String> addArticleData(@RequestBody @Validated(ValidationGroups.Create.class) ArticleFormDTO articledFormDTO) {
        articledFormDTO.setId(null);
        articleService.addArticleData(articledFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}/{is_del}")
    @Operation(summary = "删除文章")
    public Result<String> delArticleData(@PathVariable Integer id, @PathVariable Integer is_del) {
        articleService.delArticleData(id, is_del);
        return Result.success();
    }

    @PatchMapping("/reduction/{id}")
    @Operation(summary = "还原被删除的文章")
    public Result<String> recoveryArticleData(@PathVariable Integer id) {
        articleService.recoveryArticleData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文章")
    public Result<String> batchDelArticleData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        articleService.delBatchArticleData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑文章")
    public Result<String> editArticleData(@RequestBody @Validated(ValidationGroups.Update.class) ArticleFormDTO articleFormDTO) {
        articleService.editArticleData(articleFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取文章")
    public Result<ArticleVO> getArticleData(@PathVariable Integer id, @RequestParam(defaultValue = "") String password) {
        password = !password.isEmpty() ? password : "";
        ArticleVO data = articleService.getArticleData(id, password);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取文章列表")
    public Result<Map<String, Object>> getArticleList(ArticleFilterDTO articleFilterDTO) {
        Page<ArticleVO> list = articleService.getArticleList(articleFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/cate/{cate_id}")
    @Operation(summary = "获取指定分类的文章")
    public Result<Map<String, Object>> getCateArticleList(@PathVariable Integer cate_id, PageDTO pageDTO) {
        Page<ArticleVO> list = articleService.getCateArticleList(cate_id, pageDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/tag/{tag_id}")
    @Operation(summary = "获取指定标签的文章")
    public Result<Map<String, Object>> getTagArticleList(@PathVariable Integer tag_id, PageDTO pageDTO) {
        Page<ArticleVO> list = articleService.getTagArticleList(tag_id, pageDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/hot")
    @Operation(summary = "获取热门文章数据")
    public Result<List<ArticleVO>> getHotArticleList(@Parameter(description = "默认浏览量最高的5篇文章，可以通过count指定数量") @RequestParam(defaultValue = "5") Integer count) {
        List<ArticleVO> data = articleService.getHotArticleList(count);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/random")
    @Operation(summary = "随机获取文章数据")
    public Result<List<ArticleVO>> getRandomArticlesList(@Parameter(description = "默认随机获取5篇文章，可以通过count指定数量") @RequestParam(defaultValue = "5") Integer count) {
        List<ArticleVO> data = articleService.getRandomArticleList(count);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/view/{article_id}")
    @Operation(summary = "递增文章浏览量")
    public Result<String> recordViewArticleData(@PathVariable Integer article_id) {
        articleService.recordViewArticleData(article_id);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @PostMapping("/{id}/like")
    @Operation(summary = "递增文章点赞数")
    public Result<Integer> incrementArticleLike(@PathVariable Integer id, @RequestBody @Validated ArticleLikeDTO articleLikeDTO) {
        Integer likeCount = articleService.incrementArticleLike(id, articleLikeDTO.getCount());
        return Result.success(likeCount);
    }

    @NoTokenRequired
    @RateLimit
    @PostMapping("/{id}/share")
    @Operation(summary = "递增文章分享数")
    public Result<Integer> incrementArticleShare(@PathVariable Integer id, @RequestBody @Validated ArticleShareDTO articleShareDTO) {
        Integer shareCount = articleService.incrementArticleShare(id, articleShareDTO.getCount());
        return Result.success(shareCount);
    }

    @PostMapping("/import")
    @Operation(summary = "批量导入文章")
    public Result<String> importArticleList(@RequestParam MultipartFile[] list) throws IOException {
        articleService.importArticleList(list);
        return Result.success();
    }

    @PostMapping("/export")
    @Operation(summary = "批量导出文章")
    public ResponseEntity<byte[]> exportArticleList(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        return articleService.exportArticleList(ids);
    }
}
