package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileDirRenameVO", description = "重命名逻辑目录结果")
public class FileDirRenameVO {
    @ApiModelProperty(value = "原目录前缀")
    private String fromDir;

    @ApiModelProperty(value = "新目录前缀")
    private String toDir;

    @ApiModelProperty(value = "移动的对象数量")
    private Integer moved;
}
