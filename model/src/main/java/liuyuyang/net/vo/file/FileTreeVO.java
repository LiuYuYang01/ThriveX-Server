package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "FileTreeVO", description = "整桶文件目录树")
public class FileTreeVO {
    @ApiModelProperty(value = "域名根路径前缀")
    private String basePath;

    @ApiModelProperty(value = "列举到的原始对象条数（含占位对象）")
    private Integer total;

    @ApiModelProperty(value = "一级目录树根节点列表")
    private List<FileTreeNodeVO> result;
}
