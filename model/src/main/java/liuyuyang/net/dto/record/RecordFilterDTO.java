package liuyuyang.net.dto.record;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordFilterDTO extends FilterDTO {
    @Schema(description = "根据留言内容模糊查询")
    private String content;
}
