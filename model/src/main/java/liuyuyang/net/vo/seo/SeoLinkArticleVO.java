package liuyuyang.net.vo.seo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SeoLinkArticleVO {
    @Schema(description = "文章ID")
    private Integer id;

    @Schema(description = "文章标题")
    private String title;
}
