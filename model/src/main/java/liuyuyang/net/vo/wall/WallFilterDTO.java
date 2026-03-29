package liuyuyang.net.vo.wall;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;

@Data
public class WallFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "根据分类进行筛选")
    private Integer cateId;

    @ApiModelProperty(value = "0表示获取待审核的留言 | 1表示获取审核通过的留言（默认）")
    private Integer status = 1;
}
