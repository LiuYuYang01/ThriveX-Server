package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "FileCleanupScanVO", description = "未引用文件扫描结果")
public class FileCleanupScanVO {
    @Schema(description = "清理候选文件列表")
    private List<FileCleanupItemVO> candidates;

    @Schema(description = "候选文件数量")
    private Integer count;

    @Schema(description = "候选文件总体积（字节）")
    private Long totalSize;

    @Schema(description = "扫描时间（毫秒时间戳）")
    private Long scanTime;
}
