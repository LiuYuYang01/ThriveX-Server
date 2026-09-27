package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.record.RecordCommentFilterDTO;
import liuyuyang.net.dto.record.RecordCommentFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.record.RecordCommentVO;
import liuyuyang.net.web.service.CaptchaService;
import liuyuyang.net.web.service.RecordCommentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "说说评论管理")
@RestController
@RequestMapping("/record/comment")
@Transactional
@Validated
public class RecordCommentController {
    @Resource
    private RecordCommentService recordCommentService;
    @Resource
    private CaptchaService captchaService;

    @NoTokenRequired
    @RateLimit
    @PostMapping
    @Operation(summary = "新增说说评论")
    public Result<String> addRecordCommentData(@RequestBody @Validated(ValidationGroups.Create.class) RecordCommentFormDTO recordCommentFormDTO) throws Exception {
        captchaService.check(recordCommentFormDTO.getCaptchaToken());
        recordCommentFormDTO.setId(null);
        recordCommentService.addRecordCommentData(recordCommentFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除说说评论")
    public Result<String> delRecordCommentData(@PathVariable Integer id) {
        recordCommentService.delRecordCommentData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除说说评论")
    public Result<String> batchDelRecordCommentData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        recordCommentService.batchDelRecordCommentData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑说说评论")
    public Result<String> editRecordCommentData(@RequestBody @Validated(ValidationGroups.Update.class) RecordCommentFormDTO recordCommentFormDTO) {
        recordCommentService.editRecordCommentData(recordCommentFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取说说评论")
    public Result<RecordCommentVO> getRecordCommentData(@PathVariable Integer id) {
        RecordCommentVO data = recordCommentService.getRecordCommentData(id);
        return Result.success(data);
    }

    @GetMapping
    @Operation(summary = "获取说说评论列表")
    public Result<Map<String, Object>> getRecordCommentList(RecordCommentFilterDTO recordCommentFilterDTO) {
        return Result.success(Paging.filter(recordCommentService.getRecordCommentList(recordCommentFilterDTO)));
    }

    @PatchMapping("/audit/{id}")
    @Operation(summary = "审核说说评论")
    public Result<String> auditRecordCommentData(@PathVariable Integer id) {
        recordCommentService.auditRecordCommentData(id);
        return Result.success();
    }
}
