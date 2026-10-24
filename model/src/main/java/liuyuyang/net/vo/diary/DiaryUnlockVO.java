package liuyuyang.net.vo.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiaryUnlockVO {
    @Schema(description = "阅读令牌，由前端写入 HttpOnly Cookie，不回传给页面脚本")
    private String token;

    @Schema(description = "有效期（秒）", example = "604800")
    private Long maxAge;
}
