package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@TableName(value = "backup_record", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class BackupRecord extends BaseModel {
    @Schema(description = "备份类型：manual 手动 / scheduled 定时（定时备份预留）", example = "manual")
    private String type;

    @Schema(description = "导出格式：json / sql（SQL 格式预留）", example = "json")
    private String format;

    @Schema(description = "备份文件名", example = "thrivex-backup-20260926-153000-a1b2c3.json")
    private String fileName;

    @Schema(description = "文件大小（字节）", example = "1048576")
    private Long size;

    @Schema(description = "文件 SHA-256 校验值", example = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
    private String checksum;

    @Schema(description = "存储位置：local 本盘 / 第三方对象存储预留", example = "local")
    private String storage;

    @Schema(description = "存储 key（本地存储为文件名，不含路径）")
    private String storageKey;

    @Schema(description = "状态：running 进行中 / success 成功 / failed 失败", example = "success")
    private String status;

    @Schema(description = "导出表数量", example = "21")
    private Integer tableCount;

    @Schema(description = "导出总行数", example = "1234")
    private Long rowTotal;

    @Schema(description = "各表行数统计", example = "{\"article\": 42}")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> tableStats;

    @Schema(description = "耗时（毫秒）", example = "850")
    private Long durationMs;

    @Schema(description = "失败原因")
    private String error;
}
