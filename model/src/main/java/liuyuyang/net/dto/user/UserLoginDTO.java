package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserLoginDTO {
    @ApiModelProperty(value = "用户账号", example = "admin", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过50个字符")
    private String username;

    @ApiModelProperty(value = "用户密码", example = "123456", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 1, max = 50, message = "密码长度必须在1到50个字符之间")
    private String password;
}
