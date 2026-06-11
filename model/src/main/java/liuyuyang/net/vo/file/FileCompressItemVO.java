package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileCompressItemVO", description = "单文件瘦身结果")
public class FileCompressItemVO {
    @ApiModelProperty(value = "对象 key 或原始入参路径")
    private String path;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "状态：queued / processing / success / skipped / failed")
    private String status;

    @ApiModelProperty(value = "七牛 pfop 任务 ID（异步处理时有值）")
    private String taskId;

    @ApiModelProperty(value = "压缩前体积（字节）")
    private Long beforeSize;

    @ApiModelProperty(value = "压缩后体积（字节）")
    private Long afterSize;

    @ApiModelProperty(value = "节省比例（0-100）")
    private Double savedPercent;

    @ApiModelProperty(value = "说明或跳过/失败原因")
    private String message;
}
