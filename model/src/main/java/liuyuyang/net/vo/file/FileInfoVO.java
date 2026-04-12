package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileInfoVO", description = "单个文件元信息")
public class FileInfoVO {
    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "对象 key（存储路径）")
    private String path;

    @ApiModelProperty(value = "文件大小（字节）")
    private Long size;

    @ApiModelProperty(value = "七牛 hash")
    private String hash;

    @ApiModelProperty(value = "MIME 类型")
    private String mimeType;

    @ApiModelProperty(value = "上传时间（毫秒时间戳）")
    private Long putTime;

    @ApiModelProperty(value = "公开访问 URL")
    private String url;
}
