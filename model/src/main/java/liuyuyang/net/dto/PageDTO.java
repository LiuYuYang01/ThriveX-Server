package liuyuyang.net.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class PageDTO {
    @Parameter(description = "页码：默认第 1 页，不传则返回全部")
    private Integer pageNum;
    @Parameter(description = "页数：默认每页 5 条，不传则返回全部")
    private Integer pageSize;
}
