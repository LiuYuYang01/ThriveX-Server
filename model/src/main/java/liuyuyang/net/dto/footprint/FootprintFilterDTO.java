package liuyuyang.net.dto.footprint;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FootprintFilterDTO extends FilterDTO {
    @Schema(description = "根据地址内容模糊查询")
    private String address;
}