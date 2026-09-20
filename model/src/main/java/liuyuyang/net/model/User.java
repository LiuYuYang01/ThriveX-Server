package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("user")
public class User extends BaseModel {
    @Schema(description = "用户账号", example = "liuyuyang", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "用户名称", example = "宇阳", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "用户介绍", example = "再渺小的星光，也有属于他的光芒!")
    private String info;

    @Schema(description = "用户邮箱", example = "liuyuyang1024@yeah.net")
    private String email;

    @Schema(description = "用户头像", example = "yuyang.jpg")
    private String avatar;
}
