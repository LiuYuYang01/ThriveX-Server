package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileDirDeleteVO", description = "删除逻辑目录结果")
public class FileDirDeleteVO {
    @ApiModelProperty(value = "被删除的目录前缀")
    private String dir;

    @ApiModelProperty(value = "删除的对象数量")
    private Integer deleted;
}
