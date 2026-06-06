package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class EditUserPassDTO {
    @ApiModelProperty(value = "旧账号", example = "admin", required = true)
    @NotBlank(message = "旧账号不能为空")
    @Size(max = 50, message = "旧账号不能超过50个字符")
    private String oldUsername;
    @ApiModelProperty(value = "新账号", example = "thrivex666", required = true)
    @NotBlank(message = "新账号不能为空")
    @Size(max = 50, message = "新账号不能超过50个字符")
    private String newUsername;
    @ApiModelProperty(value = "旧密码", required = true)
    @NotBlank(message = "旧密码不能为空")
    @Size(min = 1, max = 50, message = "旧密码长度必须在1到50个字符之间")
    private String oldPassword;
    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "新密码长度必须在6到50个字符之间")
    private String newPassword;
}
