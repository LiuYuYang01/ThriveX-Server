package liuyuyang.net.vo.comment;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;

@Data
public class CommentFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "默认为树形结构，如果设置了list模式，则查询列表结构")
    private String pattern;

    @ApiModelProperty(value = "0表示获取待审核的评论 | 1表示获取审核通过的评论（默认）")
    private Integer status = 1;

    @ApiModelProperty(value = "根据内容关键词筛选")
    private String content;
}
