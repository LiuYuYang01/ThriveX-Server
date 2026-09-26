package liuyuyang.net.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.model.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentFormDTO extends Comment {
    @Schema(description = "人机验证Token")
    @JsonProperty("h_captcha_response")
    private String captchaToken;
}
