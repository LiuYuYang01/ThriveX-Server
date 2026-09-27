package liuyuyang.net.vo.seo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeoArticleCheckVO {
    @Schema(description = "参与体检的文章总数")
    private Integer total;

    @Schema(description = "缺少描述的文章数")
    private Integer missingDescriptionTotal;

    @Schema(description = "缺少封面的文章数")
    private Integer missingCoverTotal;

    @Schema(description = "存在问题的文章列表")
    private List<SeoArticleIssueVO> articles = new ArrayList<>();
}
