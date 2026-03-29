package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "标签ID")

    private Integer id;
    @ApiModelProperty(value = "标签名称", example = "这是一个标签", required = true)
    private String name;

    @TableField(exist = false)
    @ApiModelProperty(value = "文章数量")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
}
