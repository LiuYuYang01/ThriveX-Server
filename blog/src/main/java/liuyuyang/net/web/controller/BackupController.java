package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import liuyuyang.net.core.annotation.AuditLog;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.model.BackupRecord;
import liuyuyang.net.web.service.BackupService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据库备份：一键导出全量数据（SQL）、备份记录管理、下载。
 * 下载/删除均要求管理员登录（默认 JWT 鉴权），备份文件包含用户表等敏感数据，禁止豁免鉴权。
 */
@Tag(name = "数据库备份")
@RestController
@RequestMapping("/backup")
public class BackupController {
    @Resource
    private BackupService backupService;

    @PostMapping("/export")
    @Operation(summary = "导出数据库备份", description = "全量导出为 SQL 文件（不含日志表），同步返回备份记录")
    @AuditLog(module = "数据库备份", type = "导出", description = "导出数据库备份")
    @RateLimit(tokens = 3, duration = 60, message = "备份操作过于频繁，请 60 秒后再试")
    public Result<BackupRecord> exportBackupData() {
        return Result.success("备份成功", backupService.export());
    }

    @GetMapping("/list")
    @Operation(summary = "获取备份记录列表", description = "不传 pageNum/pageSize 返回全部，传则分页")
    public Result<Map<String, Object>> getBackupList(FilterDTO filterDTO) {
        return Result.success(backupService.getBackupList(filterDTO));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载备份文件")
    public ResponseEntity<org.springframework.core.io.Resource> downloadBackupData(
            @Parameter(description = "备份记录ID", required = true) @PathVariable Integer id) {
        return backupService.downloadBackupData(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除备份", description = "删除备份文件与对应记录")
    @AuditLog(module = "数据库备份", type = "删除", description = "删除备份")
    public Result<String> delBackupData(
            @Parameter(description = "备份记录ID", required = true) @PathVariable Integer id) {
        backupService.delBackupData(id);
        return Result.success();
    }
}
