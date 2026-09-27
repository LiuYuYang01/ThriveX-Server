package liuyuyang.net.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SiteSummaryVO {
    @Schema(description = "可见文章总数")
    private Integer articleCount;

    @Schema(description = "累计总字数")
    private Long totalWords;

    @Schema(description = "累计总浏览量")
    private Long totalViews;

    @Schema(description = "累计总获赞")
    private Long totalLikes;
}
