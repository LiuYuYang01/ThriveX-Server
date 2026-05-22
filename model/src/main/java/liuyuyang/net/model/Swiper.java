package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("swiper")
public class Swiper {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "轮播图ID")
    private Integer id;
    @ApiModelProperty(value = "轮播图标题", example = "这是一个轮播图", required = true)
    private String title;
    @ApiModelProperty(value = "轮播图", example = "http://127.0.0.1:5000/1.jpg", required = true)
    private String image;
    @ApiModelProperty(value = "轮播图描述", example = "这是一个轮播图的描述")
    private String description;
    @ApiModelProperty(value = "轮播图链接", example = "/")
    private String url;
    @TableField("`order`")
    @ApiModelProperty(value = "排序，越小越靠前", example = "1")
    private Integer order;
}
