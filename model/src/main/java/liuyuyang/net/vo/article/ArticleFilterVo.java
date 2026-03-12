package liuyuyang.net.vo.article;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.vo.FilterVo;
import lombok.Data;

@Data
public class ArticleFilterVo extends FilterVo {
    @ApiModelProperty(value = "根据分类进行筛选")
    private Integer cateId;
    @ApiModelProperty(value = "根据标签进行筛选")
    private Integer tagId;
    @ApiModelProperty(value = "是否为草稿, 默认：0 | 草稿：1", example = "0")
    private Integer isDraft = 0;
    @ApiModelProperty(value = "是否为严格删除, 默认：0 | 严格删除：1", example = "0")
    private Integer isDel = 0;
}
