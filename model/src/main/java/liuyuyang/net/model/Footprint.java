package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "footprint")
public class Footprint extends BaseModel {
    @ApiModelProperty(value = "标题", example = "这是一个标题", required = true)
    private String title;
    @ApiModelProperty(value = "地址", example = "这是一个地址", required = true)
    private String address;
    @ApiModelProperty(value = "内容", example = "这是一段内容")
    private String content;
    @ApiModelProperty(value = "坐标纬度", example = "116.413782,39.902957", required = true)
    private String position;
    @TableField(typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "图片", example = "[]")
    private List<String> images;
}
