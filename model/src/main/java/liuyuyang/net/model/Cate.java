package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.enums.cate.CateTypeEnum;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@TableName("cate")
public class Cate {
    @TableId(type = IdType.AUTO)
    @Schema(description = "分类ID")
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;
    @Schema(description = "分类名称", example = "大前端", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类名称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "分类名称不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String name;
    @Schema(description = "分类链接", example = "/")
    @Size(max = 500, message = "分类链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String url;
    @Schema(description = "分类标识", example = "dqd", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类标识不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "分类标识不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String mark;
    @Schema(description = "分类级别", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类级别不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 0, message = "分类级别不能小于0", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer level;
    @Schema(description = "分类类型：cate 分类，page 页面，nav 导航", example = "cate", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类类型不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private CateTypeEnum type;
    @TableField("`order`")
    @Schema(description = "分类顺序", example = "1")
    @Min(value = 0, message = "分类顺序不能小于0", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer order;
    @TableField("is_hide")
    @Schema(description = "是否隐藏", example = "false")
    private Boolean isHide;
}
