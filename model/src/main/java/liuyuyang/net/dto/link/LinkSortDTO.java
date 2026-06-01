package liuyuyang.net.dto.link;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LinkSortDTO {
    @ApiModelProperty(value = "网站类型 ID", example = "1", required = true)
    private Integer typeId;
    @ApiModelProperty(value = "同类型下网站 ID 列表（按展示顺序）", required = true)
    private List<Integer> ids;
}
