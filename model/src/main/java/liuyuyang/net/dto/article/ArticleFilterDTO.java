package liuyuyang.net.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleFilterDTO extends FilterDTO {
    @Schema(description = "根据文章标题进行筛选")
    private String title;
    @Schema(description = "根据分类进行筛选（满足任一分类即可）", example = "[1,2]")
    private List<Integer> cateIds;
    @Schema(description = "根据标签进行筛选")
    private Integer tagId;
    @Schema(description = "是否草稿：true 仅草稿；默认 false 仅非草稿", example = "false")
    private Boolean isDraft = false;
    @Schema(description = "是否软删除：true 仅已删除；默认 false 仅未删除", example = "false")
    private Boolean isDel = false;
}
