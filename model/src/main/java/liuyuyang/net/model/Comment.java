package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@TableName("comment")
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseModel {
    @Schema(description = "评论者名称", example = "宇阳", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "昵称不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String name;

    @Schema(description = "评论者头像", example = "yuyang.jpg")
    @Size(max = 255, message = "头像链接不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String avatar;

    @Schema(description = "评论者邮箱", example = "liuyuyang1024@yeah.net")
    @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "邮箱不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String email;

    @Schema(description = "评论者网站", example = "https://liuyuyang.net")
    @Size(max = 500, message = "网站链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String url;

    @Schema(description = "评论内容", example = "这是一段评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评论内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 10000, message = "评论内容不能超过10000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    @Schema(description = "文章ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文章ID不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer articleId;

    @Schema(description = "父评论ID，一级评论为 0", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "父评论ID不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer commentId;

    @Schema(description = "审核状态：0 待审核（默认），1 审核通过", example = "1")
    private Integer status;
}
