package liuyuyang.net.dto.cate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class CateSortDTO {
    @Schema(description = "上级分类 ID，0 表示一级分类", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类级别不能为空")
    private Integer level;
    @Schema(description = "同级分类 ID 列表（按展示顺序）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分类ID列表不能为空")
    private List<Integer> ids;
}
