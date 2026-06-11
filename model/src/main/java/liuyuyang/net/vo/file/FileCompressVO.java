package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "FileCompressVO", description = "图片瘦身批量结果")
public class FileCompressVO {
    @ApiModelProperty(value = "各文件处理明细")
    private List<FileCompressItemVO> items;

    @ApiModelProperty(value = "成功数量")
    private int successCount;

    @ApiModelProperty(value = "跳过数量")
    private int skippedCount;

    @ApiModelProperty(value = "失败数量")
    private int failedCount;

    @ApiModelProperty(value = "累计节省字节数")
    private long totalSavedBytes;
}
