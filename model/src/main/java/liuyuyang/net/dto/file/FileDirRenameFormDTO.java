package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel(value = "FileDirRenameFormDTO", description = "重命名逻辑目录")
public class FileDirRenameFormDTO {
    @ApiModelProperty(value = "原目录路径", required = true)
    @NotBlank(message = "原目录路径不能为空")
    @Size(max = 200, message = "原目录路径不能超过200个字符")
    private String fromDir;

    @ApiModelProperty(value = "新目录路径", required = true)
    @NotBlank(message = "新目录路径不能为空")
    @Size(max = 200, message = "新目录路径不能超过200个字符")
    private String toDir;
}
