package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "footprint")
public class Footprint extends BaseModel {
    @ApiModelProperty(value = "标题", example = "这是一个标题", required = true)
    @NotBlank(message = "标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "标题不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;
    @ApiModelProperty(value = "地址", example = "这是一个地址", required = true)
    @NotBlank(message = "地址不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 255, message = "地址不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String address;
    @ApiModelProperty(value = "内容", example = "这是一段内容")
    @Size(max = 5000, message = "内容不能超过5000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;
    @ApiModelProperty(value = "坐标纬度", example = "116.413782,39.902957", required = true)
    @NotBlank(message = "坐标不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "坐标不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String position;
    @TableField(typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "图片", example = "[]")
    @Size(max = 20, message = "图片数量不能超过20张", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private List<String> images;
}
