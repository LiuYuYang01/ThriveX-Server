package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.article.ArticleFormDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends ArticleFormDTO {
    @ApiModelProperty(value = "文章浏览量", example = "100")
    private Integer view;

    @ApiModelProperty(value = "文章评论数量", example = "20")
    private Integer comment;

    @TableField(exist = false)
    @ApiModelProperty(value = "分类列表")
    private List<Cate> cateList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "标签列表")
    private List<Tag> tagList = new ArrayList<>();

    @TableField(exist = false)
    private Map<String, Object> prev;
    @TableField(exist = false)
    private Map<String, Object> next;
}
