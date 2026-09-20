package liuyuyang.net.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterDTO extends PageDTO {
    @Schema(description = "根据开始时间进行筛选")
    private String startDate;
    @Schema(description = "根据结束时间进行筛选")
    private String endDate;
}
