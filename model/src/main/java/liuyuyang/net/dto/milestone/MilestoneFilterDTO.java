package liuyuyang.net.dto.milestone;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MilestoneFilterDTO extends FilterDTO {
    @Schema(description = "根据标题模糊查询")
    private String title;

    @Schema(description = "根据年份精确查询")
    private String year;
}
