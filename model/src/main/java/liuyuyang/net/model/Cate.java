package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.enums.cate.CateTypeEnum;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@TableName("cate")
public class Cate {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "分类ID")
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;
    @ApiModelProperty(value = "分类名称", example = "大前端", required = true)
    @NotBlank(message = "分类名称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "分类名称不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String name;
    @ApiModelProperty(value = "分类链接", example = "/")
    @Size(max = 500, message = "分类链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String url;
    @ApiModelProperty(value = "分类标识", example = "dqd", required = true)
    @NotBlank(message = "分类标识不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "分类标识不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String mark;
    @ApiModelProperty(value = "分类级别", example = "0", required = true)
    @NotNull(message = "分类级别不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 0, message = "分类级别不能小于0", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer level;
    @ApiModelProperty(value = "分类类型：cate 分类，page 页面，nav 导航", example = "cate", required = true)
    @NotNull(message = "分类类型不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private CateTypeEnum type;
    @TableField("`order`")
    @ApiModelProperty(value = "分类顺序", example = "1")
    @Min(value = 0, message = "分类顺序不能小于0", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer order;
    @TableField("is_hide")
    @ApiModelProperty(value = "是否隐藏", example = "false")
    private Boolean isHide;
}
