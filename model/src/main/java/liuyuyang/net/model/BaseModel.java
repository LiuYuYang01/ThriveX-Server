package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class BaseModel {
    @TableId(type = IdType.AUTO)
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;
    @Schema(description = "创建时间", example = "1723533206613", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long createTime;
}
