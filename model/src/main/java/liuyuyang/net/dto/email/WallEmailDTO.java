package liuyuyang.net.dto.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WallEmailDTO extends EmailDTO {
    @ApiModelProperty(value = "邮件标题", example = "驳回通知", required = true)
    private String subject;
    @ApiModelProperty(value = "发送方", example = "张三", required = true)
    private String recipient;
    @ApiModelProperty(value = "评论时间", example = "2024年10月15日 14:44", required = true)
    private String time;
    @ApiModelProperty(value = "你的内容", example = "太赞了", required = true)
    private String your_content;
    @ApiModelProperty(value = "回复内容", example = "必须滴", required = true)
    private String reply_content;
    @ApiModelProperty(value = "文章地址", example = "https://liuyuyang.net", required = true)
    private String url;
}
