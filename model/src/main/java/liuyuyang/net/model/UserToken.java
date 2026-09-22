package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("user_token")
public class UserToken {
    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer uid;

    @Schema(description = "用户token", example = "dadaffasfefewfwf.wefwfwfwwzfwe.zfwfwefZFfw", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
}
