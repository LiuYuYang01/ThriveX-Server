package liuyuyang.net.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class ArticleShareDTO {
    @Schema(description = "本次递增的分享数", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分享数不能为空")
    @Min(value = 1, message = "分享数至少为 1")
    @Max(value = 100, message = "单次最多分享 100 次")
    private Integer count;
}
