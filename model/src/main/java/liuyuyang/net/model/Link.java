package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@TableName("link")
public class Link extends BaseModel {
    @NotBlank(message = "网站标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "网站标题不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "网站标题", example = "这是一个网站", required = true)
    private String title;

    @NotBlank(message = "网站描述不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "网站描述不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "网站描述", example = "这是一个网站的描述", required = true)
    private String description;

    @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "邮箱不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "网站邮箱", example = "liuyuyang1024@yeah.net")
    private String email;

    @NotNull(message = "网站类型不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "网站类型", example = "1", required = true)
    private Integer typeId;

    @TableField(exist = false)
    private LinkType type;

    @NotBlank(message = "网站图片不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "网站图片链接不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "网站图片", example = "http://127.0.0.1:5000/1.jpg", required = true)
    private String image;

    @NotBlank(message = "网站链接不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500, message = "网站链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "网站链接", example = "/", required = true)
    private String url;

    @Size(max = 500, message = "RSS地址不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "订阅地址", example = "/")
    private String rss;

    @ApiModelProperty(value = "评论是否审核通过", example = "1")
    private Integer auditStatus;

    @TableField("`order`")
    @ApiModelProperty(value = "网站顺序", example = "1")
    private Integer order;
}
