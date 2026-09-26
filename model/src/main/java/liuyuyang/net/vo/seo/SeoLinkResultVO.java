package liuyuyang.net.vo.seo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeoLinkResultVO {
    @Schema(description = "链接地址")
    private String url;

    @Schema(description = "链接是否可用")
    private Boolean ok;

    @Schema(description = "HTTP 状态码，请求异常时为空")
    private Integer status;

    @Schema(description = "结果说明，如超时、域名解析失败等")
    private String message;

    @Schema(description = "引用该链接的文章")
    private List<SeoLinkArticleVO> articles = new ArrayList<>();
}
