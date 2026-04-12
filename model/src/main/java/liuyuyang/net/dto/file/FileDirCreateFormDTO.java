package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileDirCreateFormDTO", description = "新增逻辑目录")
public class FileDirCreateFormDTO {
    @ApiModelProperty(value = "业务相对目录路径", required = true)
    private String dir;
}
