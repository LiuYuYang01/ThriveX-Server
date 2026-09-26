package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.core.backup.BackupStorage;
import liuyuyang.net.core.backup.DataExporter;
import liuyuyang.net.core.backup.JsonDataExporter;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.dto.backup.BackupExportDTO;
import liuyuyang.net.model.BackupRecord;
import liuyuyang.net.web.mapper.BackupRecordMapper;
import liuyuyang.net.web.service.BackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 备份编排：生成记录（running）→ 委托导出器写文件 → 落位 → 更新（success/failed）。
 * 刻意不加类级 @Transactional：导出是长事务读，记录的插/改必须独立提交，
 * 否则导出失败回滚会把失败记录也一并吞掉；导出自身的一致性由导出器的 readOnly 事务保证。
 */
@Slf4j
@Service
public class BackupServiceImpl implements BackupService {
    private static final String TYPE_MANUAL = "manual";
    private static final String STATUS_RUNNING = "running";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILED = "failed";
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final int ERROR_MAX_LENGTH = 1000;

    private final BackupRecordMapper backupRecordMapper;
    private final BackupStorage backupStorage;
    private final Map<String, DataExporter> exporters;

    // 导出器按 format() 自动装配：后续新增 SQL 格式等实现只需注册为 Bean 即被识别
    public BackupServiceImpl(BackupRecordMapper backupRecordMapper,
                             BackupStorage backupStorage,
                             List<DataExporter> exporterList) {
        this.backupRecordMapper = backupRecordMapper;
        this.backupStorage = backupStorage;
        this.exporters = exporterList.stream()
                .collect(Collectors.toMap(DataExporter::format, Function.identity()));
    }

    @Override
    public BackupRecord export(BackupExportDTO dto) {
        String format = dto == null || !StringUtils.hasText(dto.getFormat())
                ? JsonDataExporter.FORMAT
                : dto.getFormat().trim().toLowerCase();
        DataExporter exporter = exporters.get(format);
        if (exporter == null) {
            throw new CustomException("暂不支持 " + format + " 格式的备份导出");
        }

        String fileName = buildFileName(format);
        BackupRecord record = new BackupRecord();
        record.setType(TYPE_MANUAL);
        record.setFormat(format);
        record.setStatus(STATUS_RUNNING);
        record.setStorage(backupStorage.type());
        record.setFileName(fileName);
        // 占位：本地实现落位后 key 即文件名；远程存储实现落位后更新为实际 key
        record.setStorageKey(fileName);
        record.setCreateTime(System.currentTimeMillis());
        backupRecordMapper.insert(record);

        long start = System.currentTimeMillis();
        Path tempFile = null;
        try {
            tempFile = backupStorage.newTempFile(fileName);
            DataExporter.ExportResult result = exporter.export(tempFile);
            long size = Files.size(tempFile);
            String key = backupStorage.store(tempFile, fileName);
            tempFile = null; // 已被移走，失败时不再尝试清理

            record.setStatus(STATUS_SUCCESS);
            record.setFileName(fileName);
            record.setStorageKey(key);
            record.setSize(size);
            record.setChecksum(result.checksum());
            record.setTableCount(result.tableCount());
            record.setRowTotal(result.rowTotal());
            record.setTableStats(new LinkedHashMap<>(result.tableStats()));
            record.setDurationMs(System.currentTimeMillis() - start);
            backupRecordMapper.updateById(record);
            log.info("数据库备份成功：{}（{} 字节，耗时 {}ms）", fileName, size, record.getDurationMs());
            return record;
        } catch (Exception e) {
            log.error("数据库备份失败：{}", fileName, e);
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception ignored) {
                }
            }
            // 初始记录插入失败（如数据库不可用）时无记录可更新，直接抛出原异常
            if (record.getId() != null) {
                record.setStatus(STATUS_FAILED);
                record.setDurationMs(System.currentTimeMillis() - start);
                record.setError(abbreviate(e.getMessage()));
                backupRecordMapper.updateById(record);
            }
            throw new CustomException("备份失败：" + (StringUtils.hasText(e.getMessage()) ? e.getMessage() : "未知错误"));
        }
    }

    @Override
    public Map<String, Object> getBackupList(FilterDTO filterDTO) {
        Page<BackupRecord> page = new Page<>(filterDTO.getPageNum(), filterDTO.getPageSize());
        backupRecordMapper.selectPage(page, new LambdaQueryWrapper<BackupRecord>().orderByDesc(BackupRecord::getId));
        return Paging.filter(page);
    }

    @Override
    public ResponseEntity<Resource> downloadBackupData(Integer id) {
        BackupRecord record = requireRecord(id);
        if (!STATUS_SUCCESS.equals(record.getStatus())) {
            throw new CustomException("该备份未成功生成，无法下载");
        }
        try {
            InputStream in = backupStorage.load(record.getStorageKey());
            ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + record.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM);
            if (record.getSize() != null) {
                builder.contentLength(record.getSize());
            }
            return builder.body(new InputStreamResource(in));
        } catch (IOException e) {
            throw new CustomException("读取备份文件失败：" + e.getMessage());
        }
    }

    @Override
    public void delBackupData(Integer id) {
        BackupRecord record = requireRecord(id);
        backupStorage.delete(record.getStorageKey());
        backupRecordMapper.deleteById(id);
    }

    private BackupRecord requireRecord(Integer id) {
        BackupRecord record = backupRecordMapper.selectById(id);
        if (record == null) {
            throw new CustomException("备份记录不存在");
        }
        return record;
    }

    private String buildFileName(String format) {
        return "thrivex-backup-" + LocalDateTime.now().format(FILE_TIME)
                + "-" + UUID.randomUUID().toString().substring(0, 6) + "." + format;
    }

    private String abbreviate(String message) {
        if (message == null) return null;
        return message.length() <= ERROR_MAX_LENGTH ? message : message.substring(0, ERROR_MAX_LENGTH);
    }
}
