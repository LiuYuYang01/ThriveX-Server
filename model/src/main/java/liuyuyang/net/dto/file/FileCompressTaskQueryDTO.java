package liuyuyang.net.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(value = "FileCompressTaskQueryDTO", description = "批量查询瘦身任务状态")
public class FileCompressTaskQueryDTO {
    @ApiModelProperty(value = "七牛 pfop persistentId 列表", required = true)
    @NotEmpty(message = "任务 ID 列表不能为空")
    @Size(max = 50, message = "单次最多查询 50 个任务")
    private List<@NotBlank(message = "任务 ID 不能为空") String> taskIds;
}
