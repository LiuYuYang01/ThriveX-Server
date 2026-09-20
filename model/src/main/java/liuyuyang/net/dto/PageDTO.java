package liuyuyang.net.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageDTO {
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum;
    @Schema(description = "每页条数", defaultValue = "5")
    private Integer pageSize;
}
