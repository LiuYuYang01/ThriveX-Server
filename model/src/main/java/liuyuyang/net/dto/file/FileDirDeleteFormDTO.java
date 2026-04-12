package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileDirDeleteFormDTO", description = "删除逻辑目录")
public class FileDirDeleteFormDTO {
    @ApiModelProperty(value = "要删除的目录路径", required = true)
    private String dir;
}
