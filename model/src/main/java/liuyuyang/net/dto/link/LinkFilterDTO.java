package liuyuyang.net.dto.link;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.enums.link.LinkStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LinkFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "根据网站标题进行筛选")
    private String title;

    @ApiModelProperty(value = "0表示获取待审核的友联 | 1表示获取审核通过的友联（默认）")
    private LinkStatusEnum status = LinkStatusEnum.APPROVED;
}
