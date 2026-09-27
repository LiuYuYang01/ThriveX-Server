package liuyuyang.net.vo.seo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeoLinkCheckVO {
    @Schema(description = "扫描的文章总数")
    private Integer articleTotal;

    @Schema(description = "提取到的去重链接总数")
    private Integer linkTotal;

    @Schema(description = "实际检测的链接数")
    private Integer checkedTotal;

    @Schema(description = "死链数量")
    private Integer brokenTotal;

    @Schema(description = "是否因超出单次检测上限而截断")
    private Boolean truncated;

    @Schema(description = "检测结果，异常链接排在前面")
    private List<SeoLinkResultVO> links = new ArrayList<>();
}
