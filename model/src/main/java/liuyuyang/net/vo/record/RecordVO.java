package liuyuyang.net.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.model.Record;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordVO extends Record {

    @Schema(description = "审核通过的评论数", example = "3")
    private Integer commentCount;
}
