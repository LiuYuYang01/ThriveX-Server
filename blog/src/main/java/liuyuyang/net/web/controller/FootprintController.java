package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.footprint.FootprintFilterDTO;
import liuyuyang.net.dto.footprint.FootprintFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.footprint.FootprintVO;
import liuyuyang.net.web.service.FootprintService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "足迹管理")
@RestController
@RequestMapping("/footprint")
@Transactional
@Validated
public class FootprintController {
    @Resource
    private FootprintService footprintService;

    @PostMapping
    @Operation(summary = "新增足迹")
    public Result<String> addFootprintData(@RequestBody @Validated(ValidationGroups.Create.class) FootprintFormDTO footprintFormDTO) {
        footprintFormDTO.setId(null);
        footprintService.addFootprintData(footprintFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除足迹")
    public Result<String> delFootprintData(@PathVariable Integer id) {
        footprintService.delFootprintData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除足迹")
    public Result<String> batchDelFootprintData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        footprintService.batchDelFootprintData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑足迹")
    public Result<String> editFootprintData(@RequestBody @Validated(ValidationGroups.Update.class) FootprintFormDTO footprintFormDTO) {
        footprintService.editFootprintData(footprintFormDTO);
        return Result.success();
    }

    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取足迹")
    public Result<FootprintVO> getFootprintData(@PathVariable Integer id) {
        FootprintVO data = footprintService.getFootprintData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取足迹列表")
    public Result<Map<String, Object>> getFootprintList(FootprintFilterDTO filterVo) {
        Page<FootprintVO> data = footprintService.getFootprintList(filterVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
