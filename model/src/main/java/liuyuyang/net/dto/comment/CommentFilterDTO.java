package liuyuyang.net.dto.comment;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "展示模式：tree 树形结构（默认），list 列表结构")
    private String pattern;

    @ApiModelProperty(value = "评论状态：0 待审核（默认），1 审核通过")
    private Integer status = 1;

    @ApiModelProperty(value = "根据评论内容模糊查询")
    private String content;
}
