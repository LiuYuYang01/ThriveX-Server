package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.record.RecordFilterDTO;
import liuyuyang.net.dto.record.RecordFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.record.RecordCommentVO;
import liuyuyang.net.vo.record.RecordVO;
import liuyuyang.net.web.service.RecordCommentService;
import liuyuyang.net.web.service.RecordService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "说说管理")
@RestController
@RequestMapping("/record")
@Transactional
public class RecordController {
    @Resource
    private RecordService recordService;
    @Resource
    private RecordCommentService recordCommentService;

    @PostMapping
    @ApiOperation("新增说说")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addRecordData(@RequestBody @Validated(ValidationGroups.Create.class) RecordFormDTO recordFormDTO) {
        recordFormDTO.setId(null);
        recordService.addRecordData(recordFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除说说")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delRecordData(@PathVariable Integer id) {
        recordService.delRecordData(id);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑说说")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editRecordData(@RequestBody @Validated(ValidationGroups.Update.class) RecordFormDTO recordFormDTO) {
        recordService.editRecordData(recordFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取说说")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<RecordVO> getRecordData(@PathVariable Integer id) {
        RecordVO data = recordService.getRecordData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取说说列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getRecordList(RecordFilterDTO recordFilterDTO) {
        Page<RecordVO> list = recordService.getRecordList(recordFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}/comment")
    @ApiOperation("获取指定说说下的评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<Map<String, Object>> getRecordCommentList(@PathVariable Integer id, PageDTO pageDTO) {
        Page<RecordCommentVO> list = recordCommentService.getRecordCommentListByRecordId(id, pageDTO);
        return Result.success(Paging.filter(list));
    }
}
