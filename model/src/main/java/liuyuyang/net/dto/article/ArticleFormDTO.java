package liuyuyang.net.dto.article;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.Article;
import liuyuyang.net.model.ArticleConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleFormDTO extends Article {
    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的分类ID", example = "1,2,3")
    private List<Integer> cateIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的标签ID", example = "1,2,3")
    private List<Integer> tagIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "文章配置项")
    private ArticleConfig config;
}
