package liuyuyang.net.dto.backup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BackupExportDTO {
    @Schema(description = "导出格式，缺省为 json（sql 格式预留，当前传入会拒绝）", example = "json")
    private String format;
}
