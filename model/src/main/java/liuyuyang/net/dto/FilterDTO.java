package liuyuyang.net.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterDTO extends PageDTO {
    @ApiModelProperty(value = "根据开始时间进行筛选")
    private String startDate;
    @ApiModelProperty(value = "根据结束时间进行筛选")
    private String endDate;
}
