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
@TableName("wall")
public class Wall extends BaseModel {
    @NotBlank(message = "留言人昵称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "昵称不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "留言人名称", example = "神秘人", required = true)
    private String name;

    @NotNull(message = "留言分类不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "分类id", example = "1", required = true)
    private Integer cateId;

    @TableField(exist = false)
    @ApiModelProperty(value = "留言分类", example = "全部")
    private WallCate cate;

    @Size(max = 100, message = "颜色值不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "留言墙颜色", example = "#92e6f54d")
    private String color;

    @NotBlank(message = "留言内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "留言内容不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "留言内容", example = "这是一段内容", required = true)
    private String content;

    @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "邮箱不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "邮箱", example = "3311118881@qq.com")
    private String email;

    @ApiModelProperty(value = "评论是否审核通过", example = "1")
    private Integer auditStatus;

    @ApiModelProperty(value = "设置与取消精选", example = "1")
    private Integer isChoice;
}
