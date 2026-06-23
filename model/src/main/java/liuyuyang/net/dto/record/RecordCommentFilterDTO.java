package liuyuyang.net.dto.record;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordCommentFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "评论状态：0 待审核，1 审核通过")
    private Integer status;

    @ApiModelProperty(value = "说说ID")
    private Integer recordId;

    @ApiModelProperty(value = "根据评论内容模糊查询")
    private String content;
}
