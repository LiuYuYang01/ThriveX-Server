package liuyuyang.net.dto.footprint;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FootprintFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "根据地址内容模糊查询")
    private String address;
}