package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@TableName("assistant")
public class Assistant {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;
    @TableField("`key`")
    @ApiModelProperty(value = "API 密钥", example = "xxxxxxxxxxxxxxxxxxxxxxxxxx")
    @Size(max = 200, message = "API密钥不能超过200个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String key;
    @ApiModelProperty(value = "API 地址", example = "https://api.deepseek.com")
    @NotBlank(message = "API地址不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500, message = "API地址不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String url;
    @ApiModelProperty(value = "API 模型", example = "deepseek-chat")
    @NotBlank(message = "API模型不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "API模型不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String model;
    @ApiModelProperty(value = "设置默认助手", example = "默认：0，选择：1")
    private Integer isDefault = 0;
}
