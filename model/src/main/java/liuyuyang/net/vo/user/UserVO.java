package liuyuyang.net.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// 独立声明字段而非继承 User，确保密码哈希不会出现在接口响应中
@Data
public class UserVO {
    @Schema(description = "用户ID")
    private Integer id;

    @Schema(description = "用户账号", example = "liuyuyang")
    private String username;

    @Schema(description = "用户名称", example = "宇阳", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "用户介绍", example = "再渺小的星光，也有属于他的光芒!")
    private String info;

    @Schema(description = "用户邮箱", example = "liuyuyang1024@yeah.net")
    private String email;

    @Schema(description = "用户头像", example = "yuyang.jpg")
    private String avatar;
}
