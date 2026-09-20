package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.milestone.MilestoneFilterDTO;
import liuyuyang.net.dto.milestone.MilestoneFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.milestone.MilestoneVO;
import liuyuyang.net.web.service.MilestoneService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "人生里程碑管理")
@RestController
@RequestMapping("/milestone")
@Transactional
@Validated
public class MilestoneController {
    @Resource
    private MilestoneService milestoneService;

    @PostMapping
    @Operation(summary = "新增里程碑")
    public Result<String> addMilestoneData(@RequestBody @Validated(ValidationGroups.Create.class) MilestoneFormDTO milestoneFormDTO) {
        milestoneFormDTO.setId(null);
        milestoneService.addMilestoneData(milestoneFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除里程碑")
    public Result<String> delMilestoneData(@PathVariable Integer id) {
        milestoneService.delMilestoneData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除里程碑")
    public Result<String> batchDelMilestoneData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        milestoneService.batchDelMilestoneData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑里程碑")
    public Result<String> editMilestoneData(@RequestBody @Validated(ValidationGroups.Update.class) MilestoneFormDTO milestoneFormDTO) {
        milestoneService.editMilestoneData(milestoneFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取里程碑列表", description = "不传 pageNum/pageSize 返回全部，传则分页")
    public Result<Map<String, Object>> getMilestoneList(MilestoneFilterDTO milestoneFilterDTO) {
        Page<MilestoneVO> data = milestoneService.getMilestoneList(milestoneFilterDTO);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取里程碑详情")
    public Result<MilestoneVO> getMilestoneData(@PathVariable Integer id) {
        MilestoneVO data = milestoneService.getMilestoneData(id);
        return Result.success(data);
    }
}
