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
    @ApiModelProperty(value = "是否草稿：true 仅草稿；默认 false 仅非草稿", example = "false")
    private Boolean isDraft = false;
    @ApiModelProperty(value = "是否软删除：true 仅已删除；默认 false 仅未删除", example = "false")
    private Boolean isDel = false;
}
