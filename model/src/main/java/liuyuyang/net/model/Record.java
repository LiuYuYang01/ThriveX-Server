package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("record")
public class Record extends BaseModel {
    @Schema(description = "内容", example = "大前端永远滴神！", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 10000, message = "内容不能超过10000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;
    @Schema(description = "图片", example = "[]")
    @Size(max = 5000, message = "图片数据不能超过5000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String images;
    @Schema(description = "点赞数", example = "0")
    private Integer likeCount;
    @Schema(description = "心情", example = "😊")
    @Size(max = 16, message = "心情不能超过16个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String mood;
    @Schema(description = "位置", example = "厦门市 · 环岛路")
    @Size(max = 255, message = "位置不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String location;
}
