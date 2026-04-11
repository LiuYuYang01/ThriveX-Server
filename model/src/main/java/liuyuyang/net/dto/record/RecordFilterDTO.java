package liuyuyang.net.dto.record;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordFilterDTO extends FilterDTO {
    @ApiModelProperty(value = "根据留言内容模糊查询")
    private String content;
}
