package liuyuyang.net.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.RateLimit;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.dto.cate.CateFormDTO;
import liuyuyang.net.model.Cate;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.result.cate.CateArticleCount;
import liuyuyang.net.web.service.ArticleService;
import liuyuyang.net.web.service.CateService;
import liuyuyang.net.common.utils.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "分类管理")
@RestController
@RequestMapping("/cate")
@Transactional
public class CateController {
    @Resource
    private CateService cateService;
    @Autowired
    private ArticleService articleService;

    @PostMapping
    @ApiOperation("新增分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody CateFormDTO cateFormDTO) {
        Cate cate = BeanUtil.copyProperties(cateFormDTO, Cate.class);
        cateService.save(cate);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> batchDel(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomException(400, "请提供要删除的分类 ID");
        }
        cateService.batchDel(ids);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> del(@PathVariable Integer id) {
        cateService.del(id);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody CateFormDTO cateFormDTO) {
        Cate cate = BeanUtil.copyProperties(cateFormDTO, Cate.class);
        cateService.updateById(cate);
        return Result.success();
    }

    @RateLimit
    @GetMapping("/article/count")
    @ApiOperation("获取每个分类中的文章数量")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<List<CateArticleCount>> cateArticleCount() {
        List<CateArticleCount> list = cateService.cateArticleCount();
        return Result.success(list);
    }

    @RateLimit
    @NoTokenRequired
    @GetMapping
    @ApiOperation(value = "获取分类列表", notes = "pattern: list 扁平数组 | tree 树形结构；传 page/size 时分页返回，否则返回全部")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<?> list(
            @ApiParam(value = "list: 扁平数组 | tree: 树形结构") @RequestParam(required = false, defaultValue = "list") String pattern,
            @ApiParam(value = "页码") @RequestParam(required = false) Integer page,
            @ApiParam(value = "每页数量") @RequestParam(required = false) Integer size) {
        boolean hasPage = page != null && size != null;
        Integer p = hasPage ? Math.max(1, page) : null;
        Integer s = hasPage ? Math.max(1, size) : null;

        if (hasPage) {
            Object data = cateService.list(pattern, p, s);
            if (data instanceof Page) {
                @SuppressWarnings("unchecked")
                Page<Cate> list = (Page<Cate>) data;
                Map<String, Object> result = Paging.filter(list);
                return Result.success(result);
            }
            // 兜底：如果服务端没有按约定返回 Page，直接原样返回
            return Result.success(data);
        }

        // 不分页时，直接返回完整分类数据（list 或 tree）
        Object data = cateService.list(pattern, null, null);
        return Result.success(data);
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<Cate> get(@PathVariable Integer id) {
        Cate data = cateService.get(id);
        return Result.success(data);
    }
}
