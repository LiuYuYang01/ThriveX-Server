package liuyuyang.net.vo.link;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;

@Data
public class LinkFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "0表示获取待审核的友联 | 1表示获取审核通过的友联（默认）")
    private Integer status = 1;
}
