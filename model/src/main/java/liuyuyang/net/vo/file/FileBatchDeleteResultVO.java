package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "FileBatchDeleteResultVO", description = "批量删除文件结果")
public class FileBatchDeleteResultVO {
    @Schema(description = "提交删除的文件总数")
    private int total;

    @Schema(description = "删除成功数量")
    private int successCount;

    @Schema(description = "删除失败数量")
    private int failedCount;

    @Schema(description = "删除失败的文件路径（最多返回 20 条）")
    private List<String> failedPaths;
}
