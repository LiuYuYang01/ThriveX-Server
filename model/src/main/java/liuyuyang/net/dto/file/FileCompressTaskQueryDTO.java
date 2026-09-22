package liuyuyang.net.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(name = "FileCompressTaskQueryDTO", description = "批量查询瘦身任务状态")
public class FileCompressTaskQueryDTO {
    @Schema(description = "七牛 pfop persistentId 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "任务 ID 列表不能为空")
    @Size(max = 50, message = "单次最多查询 50 个任务")
    private List<@NotBlank(message = "任务 ID 不能为空") String> taskIds;
}
