package liuyuyang.net.vo;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class PageVo {
    @ApiParam(value = "页码，不传时由业务决定：如文章列表返回全部，其余默认第1页")
    private Integer page;
    @ApiParam(value = "每页数量，不传时由业务决定：如文章列表返回全部，其余默认5条")
    private Integer size;
}
