package liuyuyang.net.dto.assistant;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssistantFilterDTO extends PageDTO {
    @ApiModelProperty(value = "根据助手名称进行筛选")
    private String name;
}
