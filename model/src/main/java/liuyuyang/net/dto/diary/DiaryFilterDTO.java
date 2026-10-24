package liuyuyang.net.dto.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DiaryFilterDTO extends FilterDTO {
    @Schema(description = "按标题或正文模糊查询")
    private String keyword;
}
