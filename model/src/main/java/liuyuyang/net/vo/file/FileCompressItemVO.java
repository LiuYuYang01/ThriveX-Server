package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileCompressItemVO", description = "单文件瘦身结果")
public class FileCompressItemVO {
    @Schema(description = "对象 key 或原始入参路径")
    private String path;

    @Schema(description = "文件名")
    private String name;

    @Schema(description = "状态：queued / processing / success / skipped / failed")
    private String status;

    @Schema(description = "七牛 pfop 任务 ID（异步处理时有值）")
    private String taskId;

    @Schema(description = "压缩前体积（字节）")
    private Long beforeSize;

    @Schema(description = "压缩后体积（字节）")
    private Long afterSize;

    @Schema(description = "节省比例（0-100）")
    private Double savedPercent;

    @Schema(description = "说明或跳过/失败原因")
    private String message;
}
