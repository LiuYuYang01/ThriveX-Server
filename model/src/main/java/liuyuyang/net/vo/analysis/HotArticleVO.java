package liuyuyang.net.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HotArticleVO {
    @Schema(description = "文章ID")
    private Integer id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章封面")
    private String cover;

    @Schema(description = "总浏览量")
    private Integer view;

    @Schema(description = "总点赞数")
    private Integer likeCount;

    @Schema(description = "统计周期内浏览量")
    private Integer recentViews;
}
