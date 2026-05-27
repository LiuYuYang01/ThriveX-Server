package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseModel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private String createTime;
}
