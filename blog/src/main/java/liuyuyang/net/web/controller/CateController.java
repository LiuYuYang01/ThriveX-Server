package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.cate.CateFilterDTO;
import liuyuyang.net.dto.cate.CateFormDTO;
import liuyuyang.net.model.Cate;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.vo.cate.CateVO;
import liuyuyang.net.web.service.CateService;
import liuyuyang.net.core.utils.Paging;
import org.springframework.beans.BeanUtils;
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
        cateFormDTO.setId(null);
        Cate cate = new Cate();
        BeanUtils.copyProperties(cateFormDTO, cate);
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
        Cate cate = new Cate();
        BeanUtils.copyProperties(cateFormDTO, cate);
        cateService.updateById(cate);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取分类列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getCateList(CateFilterDTO cateFilterDTO) {
        Page<CateVO> list = cateService.getCateList(cateFilterDTO);
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
