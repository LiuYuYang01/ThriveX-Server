package liuyuyang.net.vo.article;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.ArticleConfig;
import liuyuyang.net.model.Cate;
import liuyuyang.net.model.Tag;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
public class ArticleVO {
    private Integer id;

    @ApiModelProperty(value = "文章标题", example = "示例文章标题", required = true)
    private String title;

    @ApiModelProperty(value = "文章介绍", example = "示例文章介绍")
    private String description;
    public String getDescription() {
        return description == null ? "" : description;
    }

    @ApiModelProperty(value = "文章主要内容", example = "示例文章内容", required = true)
    private String content;

    @ApiModelProperty(value = "文章封面链接", example = "http://123.com/images/example.jpg")
    private String cover;
    public String getCover() {
        return cover == null ? "" : cover;
    }

    @ApiModelProperty(value = "文章配置项")
    private ArticleConfig config;

    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private String createTime;

    @ApiModelProperty(value = "文章浏览量", example = "100")
    private Integer view;

    @ApiModelProperty(value = "文章评论数量", example = "20")
    private Integer comment;

    @ApiModelProperty(value = "分类列表")
    private List<Cate> cateList = new ArrayList<>();

    @ApiModelProperty(value = "标签列表")
    private List<Tag> tagList = new ArrayList<>();

    @ApiModelProperty(value = "上一篇文章")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> prev;

    @ApiModelProperty(value = "下一篇文章")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> next;
}
