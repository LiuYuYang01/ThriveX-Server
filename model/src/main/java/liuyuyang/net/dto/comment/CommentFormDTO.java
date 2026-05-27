package liuyuyang.net.dto.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CommentFormDTO {
    @TableId(type = IdType.AUTO)
    @NotNull(message = "评论ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;

    @NotBlank(message = "昵称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "昵称不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "评论者名称", example = "宇阳", required = true)
    private String name;

    @Size(max = 255, message = "头像链接不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "评论者头像", example = "yuyang.jpg")
    private String avatar;

    @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "邮箱不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "评论者邮箱", example = "liuyuyang1024@yeah.net")
    private String email;

    @Size(max = 500, message = "网站链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "评论者网站", example = "https://blog.liuyuyang.net")
    private String url;

    @NotBlank(message = "评论内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 10000, message = "评论内容不能超过10000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "评论内容", example = "这是一段评论内容", required = true)
    private String content;

    @NotNull(message = "文章ID不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "该评论所绑定的文章ID", example = "1", required = true)
    private Integer articleId;

    @NotNull(message = "父评论ID不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "二级评论", example = "2", required = true)
    private Integer commentId;

    @ApiModelProperty(value = "评论是否审核通过", example = "1")
    private Integer auditStatus;

    @NotBlank(message = "创建时间不能为空", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "创建时间格式无效", groups = ValidationGroups.Create.class)
    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private String createTime;
}
