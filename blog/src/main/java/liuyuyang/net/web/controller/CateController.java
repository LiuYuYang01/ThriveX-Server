package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.cate.CateFilterDTO;
import liuyuyang.net.dto.cate.CateFormDTO;
import liuyuyang.net.dto.cate.CateSortDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.cate.CateVO;
import liuyuyang.net.web.service.CateService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "分类管理")
@RestController
@RequestMapping("/cate")
@Transactional
@Validated
public class CateController {
    @Resource
    private CateService cateService;

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<String> addCateData(@RequestBody @Validated(ValidationGroups.Create.class) CateFormDTO cateFormDTO) {
        cateFormDTO.setId(null);
        cateService.addCateData(cateFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<String> delCateData(@PathVariable Integer id) {
        cateService.delCateData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除分类")
    public Result<String> batchDelCateData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        cateService.batchDelCateData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑分类")
    public Result<String> editCateData(@RequestBody @Validated(ValidationGroups.Update.class) CateFormDTO cateFormDTO) {
        cateService.editCateData(cateFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取分类")
    public Result<CateVO> getCateData(@PathVariable Integer id) {
        CateVO data = cateService.getCateData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取分类列表")
    public Result<Map<String, Object>> getCateList(CateFilterDTO cateFilterDTO) {
        Page<CateVO> list = cateService.getCateList(cateFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @PatchMapping("/sort")
    @Operation(summary = "分类同级拖拽排序")
    public Result<String> sortCateData(@RequestBody @Valid CateSortDTO cateSortDTO) {
        cateService.sortCateData(cateSortDTO);
        return Result.success();
    }
}
