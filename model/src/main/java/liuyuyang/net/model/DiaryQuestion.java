package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 日记密语：一组中文问答。答案仅以 BCrypt 哈希存储，任何接口都不会回传。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("diary_question")
public class DiaryQuestion extends BaseModel {
    @Schema(description = "问题", example = "我们第一次见面是在哪条路？", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 120, message = "问题不能超过120个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String question;

    @Schema(description = "答案哈希", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("answer_hash")
    private String answerHash;
}
