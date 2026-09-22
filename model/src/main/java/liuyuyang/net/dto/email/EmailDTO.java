package liuyuyang.net.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmailDTO {
    @Schema(description = "邮件接收者", example = "3311118881@qq.com")
    private String to;
    @Schema(description = "邮件标题", example = "这是一段标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;
}
