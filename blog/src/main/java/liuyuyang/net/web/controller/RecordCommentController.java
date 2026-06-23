package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.record.RecordCommentFilterDTO;
import liuyuyang.net.dto.record.RecordCommentFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.record.RecordCommentVO;
import liuyuyang.net.web.service.RecordCommentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Api(tags = "说说评论管理")
@RestController
@RequestMapping("/record/comment")
@Transactional
@Validated
public class RecordCommentController {
    @Resource
    private RecordCommentService recordCommentService;

    @NoTokenRequired
    @RateLimit
    @PostMapping
    @ApiOperation("新增说说评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addRecordCommentData(@RequestBody @Validated(ValidationGroups.Create.class) RecordCommentFormDTO recordCommentFormDTO) {
        recordCommentFormDTO.setId(null);
        recordCommentService.addRecordCommentData(recordCommentFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除说说评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delRecordCommentData(@PathVariable Integer id) {
        recordCommentService.delRecordCommentData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除说说评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelRecordCommentData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        recordCommentService.batchDelRecordCommentData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑说说评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editRecordCommentData(@RequestBody @Validated(ValidationGroups.Update.class) RecordCommentFormDTO recordCommentFormDTO) {
        recordCommentService.editRecordCommentData(recordCommentFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取说说评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<RecordCommentVO> getRecordCommentData(@PathVariable Integer id) {
        RecordCommentVO data = recordCommentService.getRecordCommentData(id);
        return Result.success(data);
    }

    @GetMapping
    @ApiOperation("获取说说评论列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getRecordCommentList(RecordCommentFilterDTO recordCommentFilterDTO) {
        return Result.success(Paging.filter(recordCommentService.getRecordCommentList(recordCommentFilterDTO)));
    }

    @PatchMapping("/audit/{id}")
    @ApiOperation("审核说说评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<String> auditRecordCommentData(@PathVariable Integer id) {
        recordCommentService.auditRecordCommentData(id);
        return Result.success();
    }
}
