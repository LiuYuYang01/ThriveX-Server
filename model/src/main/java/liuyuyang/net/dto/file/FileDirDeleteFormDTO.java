package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel(value = "FileDirDeleteFormDTO", description = "删除逻辑目录")
public class FileDirDeleteFormDTO {
    @ApiModelProperty(value = "要删除的目录路径", required = true)
    @NotBlank(message = "目录路径不能为空")
    @Size(max = 200, message = "目录路径不能超过200个字符")
    private String dir;
}
