package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserLoginDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过50个字符")
    @ApiModelProperty(value = "用户账号", example = "liuyuyang", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 1, max = 50, message = "密码长度须在1到50个字符之间")
    @ApiModelProperty(value = "用户密码", required = true)
    private String password;
}
