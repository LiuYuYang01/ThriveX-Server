package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@TableName("comment")
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseModel {
    @ApiModelProperty(value = "评论者名称", example = "宇阳", required = true)
    @NotBlank(message = "昵称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "昵称不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String name;

    @ApiModelProperty(value = "评论者头像", example = "yuyang.jpg")
    @Size(max = 255, message = "头像链接不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String avatar;

    @ApiModelProperty(value = "评论者邮箱", example = "liuyuyang1024@yeah.net")
    @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "邮箱不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String email;

    @ApiModelProperty(value = "评论者网站", example = "https://liuyuyang.net")
    @Size(max = 500, message = "网站链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String url;

    @ApiModelProperty(value = "评论内容", example = "这是一段评论内容", required = true)
    @NotBlank(message = "评论内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 10000, message = "评论内容不能超过10000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    @ApiModelProperty(value = "文章ID", example = "1", required = true)
    @NotNull(message = "文章ID不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer articleId;

    @ApiModelProperty(value = "父评论ID，一级评论为 0", example = "0", required = true)
    @NotNull(message = "父评论ID不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer commentId;

    @ApiModelProperty(value = "审核状态：0 待审核（默认），1 审核通过", example = "1")
    private Integer status;
}
