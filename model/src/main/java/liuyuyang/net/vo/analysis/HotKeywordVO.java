package liuyuyang.net.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HotKeywordVO {
    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "搜索次数")
    private Integer count;
}
