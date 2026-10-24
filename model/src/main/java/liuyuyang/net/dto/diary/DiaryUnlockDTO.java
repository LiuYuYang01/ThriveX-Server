package liuyuyang.net.dto.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiaryUnlockDTO {
    @Schema(description = "问题ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请先选择一个问题")
    private Integer questionId;

    @Schema(description = "答案明文", example = "环岛路的那家咖啡店", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "先写下你的答案")
    @Size(max = 120, message = "答案不能超过120个字符")
    private String answer;
}
