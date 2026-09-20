package liuyuyang.net.dto.link;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class LinkSortDTO {
    @Schema(description = "网站类型 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "网站类型不能为空")
    private Integer typeId;
    @Schema(description = "同类型下网站 ID 列表（按展示顺序）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "网站ID列表不能为空")
    private List<Integer> ids;
}
