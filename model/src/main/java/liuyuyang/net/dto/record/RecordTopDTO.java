package liuyuyang.net.dto.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class RecordTopDTO {
    @Schema(description = "是否置顶", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请指定是否置顶")
    private Boolean top;
}
