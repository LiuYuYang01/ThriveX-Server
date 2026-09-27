package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import liuyuyang.net.core.annotation.AuditLog;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.operationlog.OperationLogFilterDTO;
import liuyuyang.net.vo.operationlog.OperationLogVO;
import liuyuyang.net.web.service.OperationLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/operation_log")
@Validated
public class OperationLogController {
    @Resource
    private OperationLogService operationLogService;

    @GetMapping
    @Operation(summary = "获取操作日志列表", description = "不传 pageNum/pageSize 返回全部，传则分页")
    public Result<Map<String, Object>> getOperationLogList(OperationLogFilterDTO filterDTO) {
        Page<OperationLogVO> data = operationLogService.getOperationLogList(filterDTO);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @AuditLog(module = "操作日志", type = "删除", description = "删除操作日志")
    @Operation(summary = "删除操作日志")
    public Result<String> delOperationLogData(@PathVariable Integer id) {
        operationLogService.delOperationLogData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @AuditLog(module = "操作日志", type = "删除", description = "批量删除操作日志")
    @Operation(summary = "批量删除操作日志")
    public Result<String> batchDelOperationLogData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        operationLogService.batchDelOperationLogData(ids);
        return Result.success();
    }

    @DeleteMapping("/clear")
    @AuditLog(module = "操作日志", type = "删除", description = "清空操作日志")
    @Operation(summary = "清空操作日志")
    public Result<String> clearOperationLogData() {
        operationLogService.clearOperationLog();
        return Result.success();
    }
}
