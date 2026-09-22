package liuyuyang.net.dto.record;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordCommentFilterDTO extends FilterDTO {
    @Schema(description = "评论状态：0 待审核，1 审核通过")
    private Integer status;

    @Schema(description = "说说ID")
    private Integer recordId;

    @Schema(description = "根据评论内容模糊查询")
    private String content;
}
