package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.RateLimit;
import liuyuyang.net.common.utils.Paging;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.dto.article.ArticleFormDTO;
import liuyuyang.net.model.Article;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.article.ArticleFilterVo;
import liuyuyang.net.web.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "文章管理")
@RestController
@RequestMapping("/article")
@Transactional
public class ArticleController {
    @Resource
    private ArticleService articleService;

    @PostMapping
    @ApiOperation("新增文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addArticleData(@RequestBody ArticleFormDTO articledFormDTO) {
        articleService.addArticleData(articledFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}/{is_del}")
    @ApiOperation("删除文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delArticleData(@PathVariable Integer id, @PathVariable Integer is_del) {
        articleService.delArticleData(id, is_del);
        return Result.success();
    }

    @PatchMapping("/reduction/{id}")
    @ApiOperation("还原被删除的文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> recoveryArticleData(@PathVariable Integer id) {
        articleService.recoveryArticleData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> batchDelArticleData(@RequestBody List<Integer> ids) {
        articleService.delBatchArticleData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<String> editArticleData(@RequestBody ArticleFormDTO articleFormDTO) {
        articleService.editArticleData(articleFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Article> getArticleData(@PathVariable Integer id, @RequestParam(defaultValue = "") String password) {
        password = !password.isEmpty() ? password : "";
        Article data = articleService.getArticleData(id, password);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取文章列表", notes = "不传 page/size 返回全部，传则分页（来自 filterVo）")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<Map<String, Object>> getArticleList(ArticleFilterVo filterVo, @RequestHeader(value = "Authorization", required = false) String token) {
        Page<Article> list = articleService.getArticleList(filterVo, token);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/cate/{cate_id}")
    @ApiOperation("获取指定分类的文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    public Result<Map<String, Object>> getCateArticleList(@PathVariable Integer cate_id, PageVo pageVo) {
        Page<Article> list = articleService.getCateArticleList(cate_id, pageVo);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/tag/{tag_id}")
    @ApiOperation("获取指定标签的文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 10)
    public Result<Map<String, Object>> getTagArticleList(@PathVariable Integer tag_id, PageVo pageVo) {
        Page<Article> list = articleService.getTagArticleList(tag_id, pageVo);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/hot")
    @ApiOperation("获取热门文章数据")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 12)
    public Result<List<Article>> getHotArticleList(@ApiParam(value = "默认浏览量最高的5篇文章，可以通过count指定数量") @RequestParam(defaultValue = "5") Integer count) {
        List<Article> data = articleService.getHotArticleList(count);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/random")
    @ApiOperation("随机获取文章数据")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 11)
    public Result<List<Article>> getRandomArticlesList(@ApiParam(value = "默认随机获取5篇文章，可以通过count指定数量") @RequestParam(defaultValue = "5") Integer count) {
        List<Article> data = articleService.getRandomArticleList(count);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/view/{article_id}")
    @ApiOperation("递增文章浏览量")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 13)
    public Result<String> recordViewArticleData(@PathVariable Integer article_id) {
        articleService.recordViewArticleData(article_id);
        return Result.success();
    }

    @PostMapping("/import")
    @ApiOperation("批量导入文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 14)
    public Result<String> importArticleList(@RequestParam MultipartFile[] list) throws IOException {
        articleService.importArticleList(list);
        return Result.success();
    }

    @PostMapping("/export")
    @ApiOperation("批量导出文章")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 15)
    public ResponseEntity<byte[]> exportArticleList(@RequestBody List<Integer> ids) {
        return articleService.exportArticleList(ids);
    }
}
