package liuyuyang.net.dto.cate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CateSortDTO {
    @ApiModelProperty(value = "上级分类 ID，0 表示一级分类", example = "0", required = true)
    private Integer level;
    @ApiModelProperty(value = "同级分类 ID 列表（按展示顺序）", required = true)
    private List<Integer> ids;
}
