package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseModel {
    @ApiModelProperty(value = "文章标题", example = "示例文章标题", required = true)
    @NotBlank(message = "文章标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "文章标题不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;

    @ApiModelProperty(value = "文章介绍", example = "示例文章介绍")
    @Size(max = 200, message = "文章介绍不能超过200个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String description;

    @ApiModelProperty(value = "文章主要内容", example = "示例文章内容", required = true)
    @NotBlank(message = "文章内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500000, message = "文章内容过长", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    @ApiModelProperty(value = "文章封面链接", example = "http://123.com/images/example.jpg")
    @Size(max = 300, message = "封面链接不能超过300个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cover;

    @ApiModelProperty(value = "文章浏览量", example = "100")
    private Integer view;

    @ApiModelProperty(value = "文章评论数量", example = "20")
    private Integer comment;
}
