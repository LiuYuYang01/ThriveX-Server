package liuyuyang.net.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FilterVo extends PageVo {
    @ApiModelProperty(value = "根据关键词进行筛选")
    private String key;
    @ApiModelProperty(value = "根据开始时间进行筛选")
    private String startDate;
    @ApiModelProperty(value = "根据结束时间进行筛选")
    private String endDate;
}
