package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.BaseModel;
import lombok.Data;

@Data
public class UserLoginDTO {
    @ApiModelProperty(value = "用户账号", example = "liuyuyang", required = true)
    private String username;

    @ApiModelProperty(value = "用户密码", required = true)
    private String password;
}
