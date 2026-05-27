package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditPassDTO {
    @ApiModelProperty(value = "旧账号", example = "admin", required = true)
    private String oldUsername;
    @ApiModelProperty(value = "新账号", example = "thrivex666", required = true)
    private String newUsername;
    @ApiModelProperty(value = "旧密码", required = true)
    private String oldPassword;
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;
}
