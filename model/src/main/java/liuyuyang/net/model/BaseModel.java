package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BaseModel {
    @TableId(type = IdType.AUTO)
    @NotNull(message = "ID不能为空", groups = ValidationGroups.Update.class)
    private Integer id;
    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private Long createTime;
}
