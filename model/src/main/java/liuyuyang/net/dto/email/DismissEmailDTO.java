package liuyuyang.net.dto.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DismissEmailDTO extends EmailDTO {
    @ApiModelProperty(value = "邮件标题", example = "驳回通知", required = true)
    private String subject;
    @ApiModelProperty(value = "类型", example = "友联", required = true)
    private String type;
    @ApiModelProperty(value = "接收方", example = "张三", required = true)
    private String recipient;
    @ApiModelProperty(value = "评论时间", example = "2024年10月15日 14:44", required = true)
    private String time;
    @ApiModelProperty(value = "评论内容", example = "涉嫌违规", required = true)
    private String content;
    @ApiModelProperty(value = "文章地址", example = "https://liuyuyang.net", required = true)
    private String url;
}
