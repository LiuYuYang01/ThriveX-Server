package liuyuyang.net.dto.article;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.model.Article;
import liuyuyang.net.model.ArticleConfig;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleFormDTO extends Article {
    @TableField(exist = false)
    @Schema(description = "该文章所绑定的分类ID", example = "1,2,3")
    @NotEmpty(message = "文章分类不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private List<Integer> cateIds;

    @TableField(exist = false)
    @Schema(description = "该文章所绑定的标签ID", example = "1,2,3")
    private List<Integer> tagIds;

    @TableField(exist = false)
    @Schema(description = "文章配置项")
    @NotNull(message = "文章配置不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Valid
    private ArticleConfig config;
}
