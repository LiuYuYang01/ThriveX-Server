package liuyuyang.net.dto.link;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LinkSortDTO {
    @ApiModelProperty(value = "网站类型 ID", example = "1", required = true)
    @NotNull(message = "网站类型不能为空")
    private Integer typeId;
    @ApiModelProperty(value = "同类型下网站 ID 列表（按展示顺序）", required = true)
    @NotEmpty(message = "网站ID列表不能为空")
    private List<Integer> ids;
}
