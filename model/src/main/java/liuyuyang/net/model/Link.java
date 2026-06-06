package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.enums.link.LinkStatusEnum;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("link")
public class Link extends BaseModel {
    @ApiModelProperty(value = "网站标题", example = "这是一个网站", required = true)
    @NotBlank(message = "网站标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "网站标题不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;
    @ApiModelProperty(value = "网站描述", example = "这是一个网站的描述", required = true)
    @NotBlank(message = "网站描述不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "网站描述不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String description;
    @ApiModelProperty(value = "网站邮箱", example = "liuyuyang1024@yeah.net")
    @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "邮箱不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String email;
    @ApiModelProperty(value = "网站类型", example = "1", required = true)
    @NotNull(message = "网站类型不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer typeId;
    @ApiModelProperty(value = "网站图片", example = "http://127.0.0.1:5000/1.jpg", required = true)
    @NotBlank(message = "网站图片不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "网站图片链接不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String image;
    @ApiModelProperty(value = "网站链接", example = "/", required = true)
    @NotBlank(message = "网站链接不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500, message = "网站链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String url;
    @ApiModelProperty(value = "订阅地址", example = "/")
    @Size(max = 500, message = "RSS地址不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String rss;
    @ApiModelProperty(value = "审核状态：0 待审核，1 审核通过", example = "1")
    private LinkStatusEnum status;
    @TableField("`order`")
    @ApiModelProperty(value = "网站顺序", example = "1")
    private Integer order;
}
