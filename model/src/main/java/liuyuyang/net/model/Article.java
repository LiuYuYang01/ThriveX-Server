package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseModel {
    @Schema(description = "文章标题", example = "示例文章标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文章标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "文章标题不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;

    @Schema(description = "文章介绍", example = "示例文章介绍")
    @Size(max = 200, message = "文章介绍不能超过200个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String description;

    @Schema(description = "文章主要内容", example = "示例文章内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文章内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500000, message = "文章内容过长", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    @Schema(description = "文章封面链接", example = "http://123.com/images/example.jpg")
    @Size(max = 300, message = "封面链接不能超过300个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cover;

    @Schema(description = "文章浏览量", example = "100")
    private Integer view;

    @Schema(description = "文章评论数量", example = "20")
    private Integer comment;

    @Schema(description = "点赞数", example = "0")
    private Integer likeCount;

    @Schema(description = "分享数", example = "0")
    private Integer shareCount;
}
