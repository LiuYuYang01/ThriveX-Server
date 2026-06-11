package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(value = "FileCompressFormDTO", description = "批量图片瘦身")
public class FileCompressFormDTO {
    @ApiModelProperty(value = "待瘦身文件的 path 或 URL 列表", required = true)
    @NotEmpty(message = "文件路径列表不能为空")
    @Size(max = 50, message = "单次最多处理 50 个文件")
    private List<@NotBlank(message = "文件路径不能为空") @Size(max = 500, message = "文件路径不能超过500个字符") String> paths;

    @ApiModelProperty(value = "压缩模式：auto（默认）/ light / medium / strong")
    private String mode;
}
