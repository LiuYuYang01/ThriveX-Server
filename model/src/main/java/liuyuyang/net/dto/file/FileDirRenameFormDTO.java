package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileDirRenameFormDTO", description = "重命名逻辑目录")
public class FileDirRenameFormDTO {
    @ApiModelProperty(value = "原目录路径", required = true)
    private String fromDir;

    @ApiModelProperty(value = "新目录路径", required = true)
    private String toDir;
}
