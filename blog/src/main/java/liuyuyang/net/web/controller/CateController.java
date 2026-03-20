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
import liuyuyang.net.web.service.CateService;
import liuyuyang.net.common.utils.Paging;
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

    @PostMapping
    @ApiOperation("新增分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addArticleData(@RequestBody CateFormDTO cateFormDTO) {
        Cate cate = BeanUtil.copyProperties(cateFormDTO, Cate.class);
        cateService.save(cate);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> batchDelCateData(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomException(400, "请提供要删除的分类 ID");
        }
        cateService.batchDelCateData(ids);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> delCateData(@PathVariable Integer id) {
        cateService.delCateData(id);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editArticleData(@RequestBody CateFormDTO cateFormDTO) {
        Cate cate = BeanUtil.copyProperties(cateFormDTO, Cate.class);
        cateService.updateById(cate);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/article/count")
    @ApiOperation("获取每个分类中的文章数量")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<List<CateArticleCount>> getCateArticleCount() {
        List<CateArticleCount> list = cateService.getCateArticleCount();
        return Result.success(list);
    }

    @RateLimit
    @NoTokenRequired
    @GetMapping
    @ApiOperation(value = "获取分类列表", notes = "pattern: list 扁平 | tree 树形；不传 page/size 返回全部，传则分页")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getCateList(
            @ApiParam(value = "list: 扁平数组 | tree: 树形结构") @RequestParam(required = false, defaultValue = "list") String pattern,
            @ApiParam(value = "页码，不传则返回全部") @RequestParam(required = false) Integer page,
            @ApiParam(value = "每页数量，不传则返回全部") @RequestParam(required = false) Integer size) {
        Page<Cate> list = cateService.list(pattern, page, size);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<Cate> getCateData(@PathVariable Integer id) {
        Cate data = cateService.getCateData(id);
        return Result.success(data);
    }
}
