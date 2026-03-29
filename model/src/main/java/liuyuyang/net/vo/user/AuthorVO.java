package liuyuyang.net.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthorVO {
    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户名称", example = "宇阳", required = true)
    private String name;

    @ApiModelProperty(value = "用户介绍", example = "再渺小的星光，也有属于他的光芒!")
    private String info;

    @ApiModelProperty(value = "用户邮箱", example = "liuyuyang1024@yeah.net")
    private String email;

    @ApiModelProperty(value = "用户头像", example = "yuyang.jpg")
    private String avatar;
}
