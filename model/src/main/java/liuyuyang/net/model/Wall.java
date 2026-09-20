package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.enums.wall.WallAuditStatusEnum;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wall")
public class Wall extends BaseModel {
    @Schema(description = "留言人名称", example = "神秘人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "昵称不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String name;

    @Schema(description = "分类id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer cateId;

    @TableField(exist = false)
    @Schema(description = "留言分类", example = "全部")
    private WallCate cate;

    @Schema(description = "留言墙颜色", example = "#92e6f54d")
    @Size(max = 100, message = "颜色值不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String color;

    @Schema(description = "留言内容", example = "这是一段内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 1000, message = "内容不能超过1000个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    @Schema(description = "邮箱", example = "3311118881@qq.com")
    @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "邮箱不能超过100个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String email;

    @Schema(description = "审核状态：0 待审核，1 审核通过", example = "1")
    private WallAuditStatusEnum status;

    @Schema(description = "设置与取消精选", example = "1")
    private Integer isChoice;
}
