package liuyuyang.net.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class EditUserPassDTO {
    @Schema(description = "旧账号", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "旧账号不能为空")
    @Size(max = 50, message = "旧账号不能超过50个字符")
    private String oldUsername;
    @Schema(description = "新账号", example = "thrivex666", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新账号不能为空")
    @Size(max = 50, message = "新账号不能超过50个字符")
    private String newUsername;
    @Schema(description = "旧密码，系统初始化阶段可不填")
    @Size(max = 50, message = "旧密码不能超过50个字符")
    private String oldPassword;
    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "新密码长度必须在6到50个字符之间")
    private String newPassword;
}
