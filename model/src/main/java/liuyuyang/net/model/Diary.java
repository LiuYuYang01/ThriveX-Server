package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 日记：全部私密，只能通过后台配置的密语问答解锁阅读。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("diary")
public class Diary extends BaseModel {
    @Schema(description = "日记日期", example = "2026-10-23", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "日记日期不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDate diaryDate;

    @Schema(description = "标题", example = "关于一场没下的雨")
    @Size(max = 120, message = "标题不能超过120个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;

    @Schema(description = "正文", example = "今天的风把云推得很低。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "正文不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 10000, message = "正文不能超过10000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    @Schema(description = "图片", example = "[]")
    @Size(max = 5000, message = "图片数据不能超过5000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String images;

    @Schema(description = "心情", example = "🌿")
    @Size(max = 16, message = "心情不能超过16个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String mood;

    @Schema(description = "天气", example = "rainy")
    @Size(max = 16, message = "天气不能超过16个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String weather;

    @Schema(description = "位置", example = "厦门市 · 环岛路")
    @Size(max = 255, message = "位置不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String location;

    @Schema(description = "更新时间", example = "1783153776358")
    private Long updateTime;
}
