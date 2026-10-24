package liuyuyang.net.dto.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiaryQuestionFormDTO {
    @Schema(description = "问题ID", accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;

    @Schema(description = "问题", example = "我们第一次见面是在哪条路？", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 120, message = "问题不能超过120个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String question;

    @Schema(description = "答案明文，编辑时留空表示保持不变", example = "环岛路的那家咖啡店")
    @Size(max = 120, message = "答案不能超过120个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String answer;
}
