package liuyuyang.net.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(name = "FileCompressFormDTO", description = "批量图片瘦身")
public class FileCompressFormDTO {
    @Schema(description = "待瘦身文件的 path 或 URL 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件路径列表不能为空")
    @Size(max = 50, message = "单次最多处理 50 个文件")
    private List<@NotBlank(message = "文件路径不能为空") @Size(max = 500, message = "文件路径不能超过500个字符") String> paths;

    @Schema(description = "压缩模式：auto（默认）/ light / medium / strong")
    private String mode;
}
