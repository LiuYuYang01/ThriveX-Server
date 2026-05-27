package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class BaseModel {
    @TableId(type = IdType.AUTO)
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;

    @NotBlank(message = "创建时间不能为空", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "创建时间格式无效", groups = ValidationGroups.Create.class)
    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private String createTime;
}
