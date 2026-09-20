package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@TableName("swiper")
public class Swiper {
    @TableId(type = IdType.AUTO)
    @Schema(description = "轮播图ID")
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;
    @Schema(description = "轮播图标题", example = "这是一个轮播图", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "轮播图标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "轮播图标题不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;
    @Schema(description = "轮播图", example = "http://127.0.0.1:5000/1.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "轮播图不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500, message = "轮播图链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String image;
    @Schema(description = "轮播图描述", example = "这是一个轮播图的描述")
    @Size(max = 255, message = "轮播图描述不能超过255个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String description;
    @Schema(description = "轮播图链接", example = "/")
    @Size(max = 500, message = "轮播图链接不能超过500个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String url;
    @TableField("`order`")
    @Schema(description = "排序，越小越靠前", example = "1")
    @Min(value = 0, message = "排序不能小于0", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer order;
}
