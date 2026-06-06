package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(value = "FileBatchDeleteFormDTO", description = "批量删除文件")
public class FileBatchDeleteFormDTO {
    @ApiModelProperty(value = "待删除文件的完整访问 URL 列表", required = true)
    @NotEmpty(message = "文件路径列表不能为空")
    private List<@NotBlank(message = "文件路径不能为空") @Size(max = 500, message = "文件路径不能超过500个字符") String> paths;
}
