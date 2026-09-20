package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@TableName(value = "milestone", autoResultMap = true)
public class Milestone {
 @TableId(type = IdType.AUTO)
 @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
 private Integer id;

 @Schema(description = "事件时间戳", example = "1063555200000", requiredMode = Schema.RequiredMode.REQUIRED)
 @NotNull(message = "事件时间不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
 private Long eventDate;

 @Schema(description = "标题", example = "降生于世", requiredMode = Schema.RequiredMode.REQUIRED)
 @NotBlank(message = "标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
 @Size(max =100, message = "标题不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
 private String title;

 @Schema(description = "描述", example = "人生中的重要时刻")
 @Size(max =2000, message = "描述不能超过2000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
 private String description;

 @Schema(description = "封面图", example = "https://example.com/image.jpg")
 @Size(max =500, message = "封面图地址不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
 private String image;

 @TableField(typeHandler = JacksonTypeHandler.class)
 @Schema(description = "标签", example = "[\"生命\",\"起点\"]")
 @Size(max =10, message = "标签数量不能超过10个", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
 private List<String> tags;
}
