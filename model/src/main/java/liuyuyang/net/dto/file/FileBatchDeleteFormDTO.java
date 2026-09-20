package liuyuyang.net.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(name = "FileBatchDeleteFormDTO", description = "批量删除文件")
public class FileBatchDeleteFormDTO {
    @Schema(description = "待删除文件的完整访问 URL 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件路径列表不能为空")
    private List<@NotBlank(message = "文件路径不能为空") @Size(max = 500, message = "文件路径不能超过500个字符") String> paths;
}
