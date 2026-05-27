package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("link")
public class Link extends BaseModel {
    @ApiModelProperty(value = "网站标题", example = "这是一个网站", required = true)
    private String title;
    @ApiModelProperty(value = "网站描述", example = "这是一个网站的描述", required = true)
    private String description;
    @ApiModelProperty(value = "网站邮箱", example = "liuyuyang1024@yeah.net")
    private String email;
    @ApiModelProperty(value = "网站类型", example = "1", required = true)
    private Integer typeId;
    @TableField(exist = false)
    private LinkType type;
    @ApiModelProperty(value = "网站图片", example = "http://127.0.0.1:5000/1.jpg", required = true)
    private String image;
    @ApiModelProperty(value = "网站链接", example = "/", required = true)
    private String url;
    @ApiModelProperty(value = "订阅地址", example = "/")
    private String rss;
    @ApiModelProperty(value = "评论是否审核通过", example = "1")
    private Integer auditStatus;
    @TableField("`order`")
    @ApiModelProperty(value = "网站顺序", example = "1")
    private Integer order;
}
