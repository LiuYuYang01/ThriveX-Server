package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EditUserInfoDTO {
    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "用户名称", example = "宇阳", required = true)
    @NotBlank(message = "用户名称不能为空")
    @Size(max = 50, message = "用户名称不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "用户介绍", example = "再渺小的星光，也有属于他的光芒!")
    @Size(max = 500, message = "用户介绍不能超过500个字符")
    private String info;

    @ApiModelProperty(value = "用户邮箱", example = "liuyuyang1024@yeah.net")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @ApiModelProperty(value = "用户头像", example = "yuyang.jpg")
    @Size(max = 255, message = "头像链接不能超过255个字符")
    private String avatar;
}
