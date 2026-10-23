package liuyuyang.net.dto.wall;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.model.Wall;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WallFormDTO extends Wall {
    @Schema(description = "人机验证 Token（Cloudflare Turnstile）")
    @JsonProperty("cf_turnstile_response")
    private String captchaToken;
}
