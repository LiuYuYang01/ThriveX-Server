package liuyuyang.net.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DismissEmailDTO extends EmailDTO {
    @Schema(description = "邮件标题", example = "驳回通知", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;
    @Schema(description = "类型", example = "友联", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    @Schema(description = "接收方", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipient;
    @Schema(description = "评论时间", example = "2024年10月15日 14:44", requiredMode = Schema.RequiredMode.REQUIRED)
    private String time;
    @Schema(description = "评论内容", example = "涉嫌违规", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
    @Schema(description = "文章地址", example = "https://liuyuyang.net", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;
}
