package liuyuyang.net.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.model.ArticleConfig;
import liuyuyang.net.model.Tag;
import liuyuyang.net.vo.cate.CateVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
public class ArticleVO {
    private Integer id;

    @Schema(description = "文章标题", example = "示例文章标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "文章介绍", example = "示例文章介绍")
    private String description;
    public String getDescription() {
        return description == null ? "" : description;
    }

    @Schema(description = "文章主要内容", example = "示例文章内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "文章封面链接", example = "http://123.com/images/example.jpg")
    private String cover;
    public String getCover() {
        return cover == null ? "" : cover;
    }

    @Schema(description = "文章配置项")
    private ArticleConfig config;

    @Schema(description = "文章浏览量", example = "100")
    private Integer view;

    @Schema(description = "文章评论数量", example = "20")
    private Integer comment;

    @Schema(description = "点赞数", example = "0")
    private Integer likeCount;

    @Schema(description = "分享数", example = "0")
    private Integer shareCount;

    @Schema(description = "分类列表")
    private List<CateVO> cateList = new ArrayList<>();

    @Schema(description = "标签列表")
    private List<Tag> tagList = new ArrayList<>();

    @Schema(description = "上一篇文章")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> prev;

    @Schema(description = "下一篇文章")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> next;

    @Schema(description = "创建时间", example = "1723533206613", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long createTime;
}
