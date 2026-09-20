package liuyuyang.net.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(name = "FileDirRenameFormDTO", description = "重命名逻辑目录")
public class FileDirRenameFormDTO {
    @Schema(description = "原目录路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原目录路径不能为空")
    @Size(max = 200, message = "原目录路径不能超过200个字符")
    private String fromDir;

    @Schema(description = "新目录路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新目录路径不能为空")
    @Size(max = 200, message = "新目录路径不能超过200个字符")
    private String toDir;
}
