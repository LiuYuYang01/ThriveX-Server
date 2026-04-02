package liuyuyang.net.dto;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class PageDTO {
    @ApiParam(value = "页码：默认第 1 页，不传则返回全部")
    private Integer pageNum;
    @ApiParam(value = "页数：默认每页 5 条，不传则返回全部")
    private Integer pageSize;
}
