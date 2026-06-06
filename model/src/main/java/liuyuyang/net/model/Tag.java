package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@TableName("tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "标签ID")
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;
    @ApiModelProperty(value = "标签名称", example = "这是一个标签", required = true)
    @NotBlank(message = "标签名称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "标签名称不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String name;

    @TableField(exist = false)
    @ApiModelProperty(value = "文章数量")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
}
