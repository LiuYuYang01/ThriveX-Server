package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "FileCompressVO", description = "图片瘦身批量结果")
public class FileCompressVO {
    @Schema(description = "各文件处理明细")
    private List<FileCompressItemVO> items;

    @Schema(description = "成功数量")
    private int successCount;

    @Schema(description = "跳过数量")
    private int skippedCount;

    @Schema(description = "失败数量")
    private int failedCount;

    @Schema(description = "累计节省字节数")
    private long totalSavedBytes;
}
