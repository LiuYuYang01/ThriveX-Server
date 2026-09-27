package liuyuyang.net.vo.seo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SeoArticleIssueVO {
    @Schema(description = "文章ID")
    private Integer id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "是否缺少描述")
    private Boolean missingDescription;

    @Schema(description = "是否缺少封面")
    private Boolean missingCover;
}
