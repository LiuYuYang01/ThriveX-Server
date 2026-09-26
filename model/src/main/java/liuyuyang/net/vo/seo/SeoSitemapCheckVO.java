package liuyuyang.net.vo.seo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeoSitemapCheckVO {
    @Schema(description = "sitemap 是否可达")
    private Boolean reachable;

    @Schema(description = "sitemap 地址")
    private String url;

    @Schema(description = "不可达或配置缺失时的说明")
    private String message;

    @Schema(description = "sitemap 中的 URL 总数")
    private Integer sitemapTotal;

    @Schema(description = "应有文章数（公开范围）")
    private Integer articleTotal;

    @Schema(description = "sitemap 中收录的文章 URL 数")
    private Integer sitemapArticleCount;

    @Schema(description = "未收录进 sitemap 的文章")
    private List<SeoArticleIssueVO> missingArticles = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "未收录进 sitemap 的静态页面路径")
    private List<String> missingStaticPages = new ArrayList<>();
}
