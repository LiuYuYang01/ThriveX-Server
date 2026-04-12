package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "FileBatchDeleteFormDTO", description = "批量删除文件")
public class FileBatchDeleteFormDTO {
    @ApiModelProperty(value = "待删除文件的完整访问 URL 列表", required = true)
    private List<String> paths;
}
