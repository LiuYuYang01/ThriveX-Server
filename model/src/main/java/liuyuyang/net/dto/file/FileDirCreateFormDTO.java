package liuyuyang.net.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(name = "FileDirCreateFormDTO", description = "新增逻辑目录")
public class FileDirCreateFormDTO {
    @Schema(description = "业务相对目录路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "目录路径不能为空")
    @Size(max = 200, message = "目录路径不能超过200个字符")
    private String dir;
}
