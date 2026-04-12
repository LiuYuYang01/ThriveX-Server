package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FileFilterDTO", description = "文件列表筛选与分页")
public class FileFilterDTO extends PageDTO {
    @ApiModelProperty(value = "业务相对目录（如 article），将作为存储 key 前缀下的子路径", required = true)
    private String dir;
}
