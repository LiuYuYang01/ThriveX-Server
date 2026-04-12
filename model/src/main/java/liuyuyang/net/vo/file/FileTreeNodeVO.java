package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "FileTreeNodeVO", description = "文件树中的目录节点")
public class FileTreeNodeVO {
    @ApiModelProperty(value = "节点类型：dir")
    private String type;

    @ApiModelProperty(value = "目录名")
    private String name;

    @ApiModelProperty(value = "目录路径前缀")
    private String path;

    @ApiModelProperty(value = "子目录")
    private List<FileTreeNodeVO> children = new ArrayList<>();

    @ApiModelProperty(value = "当前目录下的文件")
    private List<FileTreeFileVO> files = new ArrayList<>();

    @ApiModelProperty(value = "目录下文件数量（递归统计真实文件）")
    private Integer fileCount;

    @ApiModelProperty(value = "目录下文件总大小（字节）")
    private Long totalSize;
}
