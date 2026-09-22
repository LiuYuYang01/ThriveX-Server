package liuyuyang.net.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommentEmailDTO extends EmailDTO {
    @Schema(description = "文章标题", example = "这是一段标题", requiredMode = Schema.RequiredMode.REQUIRED)
    String title;
    @Schema(description = "发送方", example = "神秘人", requiredMode = Schema.RequiredMode.REQUIRED)
    String recipient;
    @Schema(description = "评论时间", example = "2024年10月15日 14:44", requiredMode = Schema.RequiredMode.REQUIRED)
    String time;
    @Schema(description = "评论内容", example = "这是一段内容", requiredMode = Schema.RequiredMode.REQUIRED)
    String content;
    @Schema(description = "文章地址", example = "https://liuyuyang.net", requiredMode = Schema.RequiredMode.REQUIRED)
    String url;
}
