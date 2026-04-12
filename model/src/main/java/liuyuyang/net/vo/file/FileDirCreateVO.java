package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FileDirCreateVO", description = "创建逻辑目录结果")
public class FileDirCreateVO {
    @ApiModelProperty(value = "规范化后的目录前缀")
    private String dir;

    @ApiModelProperty(value = "占位对象 key")
    private String placeholder;

    @ApiModelProperty(value = "新建目录节点，便于前端本地插入树")
    private FileTreeNodeVO node;
}
