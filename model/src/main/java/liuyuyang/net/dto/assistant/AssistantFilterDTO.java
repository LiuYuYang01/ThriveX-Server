package liuyuyang.net.dto.assistant;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssistantFilterDTO extends PageDTO {
    @Schema(description = "根据模型进行筛选")
    private String model;
}
