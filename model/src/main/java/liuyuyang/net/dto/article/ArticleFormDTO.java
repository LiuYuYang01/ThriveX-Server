package liuyuyang.net.dto.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.ArticleConfig;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ArticleFormDTO {
    @TableId(type = IdType.AUTO)
    @NotNull(message = "文章ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;

    @NotBlank(message = "文章标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "文章标题不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "文章标题", example = "示例文章标题", required = true)
    private String title;

    @Size(max = 200, message = "文章介绍不能超过200个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "文章介绍", example = "示例文章介绍")
    private String description;

    @NotBlank(message = "文章内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500000, message = "文章内容过长", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "文章主要内容", example = "示例文章内容", required = true)
    private String content;

    @Size(max = 300, message = "封面链接不能超过300个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "文章封面链接", example = "http://123.com/images/example.jpg")
    private String cover;

    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的分类ID", example = "1,2,3")
    private List<Integer> cateIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的标签ID", example = "1,2,3")
    private List<Integer> tagIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "文章配置项")
    private ArticleConfig config;

    @NotBlank(message = "创建时间不能为空", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "创建时间格式无效", groups = ValidationGroups.Create.class)
    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private String createTime;
}
