package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileListItemVO", description = "目录下列表中的单条文件")
public class FileListItemVO {
    @ApiModelProperty(value = "域名根路径前缀")
    private String basePath;

    @ApiModelProperty(value = "业务相对目录")
    private String dir;

    @ApiModelProperty(value = "对象 key（存储路径）")
    private String path;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "文件大小（字节）")
    private Long size;

    @ApiModelProperty(value = "扩展名（小写）")
    private String type;

    @ApiModelProperty(value = "上传时间（毫秒时间戳）")
    private Long date;

    @ApiModelProperty(value = "公开访问 URL")
    private String url;
}
