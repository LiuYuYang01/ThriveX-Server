package liuyuyang.net.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WallEmailDTO extends EmailDTO {
    @Schema(description = "邮件标题", example = "驳回通知", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;
    @Schema(description = "发送方", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipient;
    @Schema(description = "评论时间", example = "2024年10月15日 14:44", requiredMode = Schema.RequiredMode.REQUIRED)
    private String time;
    @Schema(description = "你的内容", example = "太赞了", requiredMode = Schema.RequiredMode.REQUIRED)
    private String your_content;
    @Schema(description = "回复内容", example = "必须滴", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reply_content;
    @Schema(description = "文章地址", example = "https://liuyuyang.net", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;
}
