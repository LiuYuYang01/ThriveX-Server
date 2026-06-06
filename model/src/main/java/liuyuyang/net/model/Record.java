package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("record")
public class Record extends BaseModel {
    @ApiModelProperty(value = "内容", example = "大前端永远滴神！", required = true)
    @NotBlank(message = "内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 10000, message = "内容不能超过10000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;
    @ApiModelProperty(value = "图片", example = "[]")
    @Size(max = 5000, message = "图片数据不能超过5000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String images;
}
