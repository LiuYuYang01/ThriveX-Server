package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.record.RecordFilterDTO;
import liuyuyang.net.dto.record.RecordFormDTO;
import liuyuyang.net.dto.record.RecordLikeDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.record.RecordCommentVO;
import liuyuyang.net.vo.record.RecordVO;
import liuyuyang.net.web.service.RecordCommentService;
import liuyuyang.net.web.service.RecordService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

@Tag(name = "闪念管理")
@RestController
@RequestMapping("/record")
@Transactional
public class RecordController {
    @Resource
    private RecordService recordService;
    @Resource
    private RecordCommentService recordCommentService;

    @PostMapping
    @Operation(summary = "新增说说")
    public Result<String> addRecordData(@RequestBody @Validated(ValidationGroups.Create.class) RecordFormDTO recordFormDTO) {
        recordFormDTO.setId(null);
        recordService.addRecordData(recordFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除说说")
    public Result<String> delRecordData(@PathVariable Integer id) {
        recordService.delRecordData(id);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑说说")
    public Result<String> editRecordData(@RequestBody @Validated(ValidationGroups.Update.class) RecordFormDTO recordFormDTO) {
        recordService.editRecordData(recordFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取说说")
    public Result<RecordVO> getRecordData(@PathVariable Integer id) {
        RecordVO data = recordService.getRecordData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取说说列表")
    public Result<Map<String, Object>> getRecordList(RecordFilterDTO recordFilterDTO) {
        Page<RecordVO> list = recordService.getRecordList(recordFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}/comment")
    @Operation(summary = "获取指定说说下的评论")
    public Result<Map<String, Object>> getRecordCommentList(@PathVariable Integer id, PageDTO pageDTO) {
        Page<RecordCommentVO> list = recordCommentService.getRecordCommentListByRecordId(id, pageDTO);
        return Result.success(Paging.filter(list));
    }

    @NoTokenRequired
    @RateLimit
    @PostMapping("/{id}/like")
    @Operation(summary = "递增说说点赞数")
    public Result<Integer> incrementRecordLike(@PathVariable Integer id, @RequestBody @Validated RecordLikeDTO recordLikeDTO) {
        Integer likeCount = recordService.incrementRecordLike(id, recordLikeDTO.getCount());
        return Result.success(likeCount);
    }
}
