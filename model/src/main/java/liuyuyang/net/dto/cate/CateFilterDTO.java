package liuyuyang.net.dto.cate;

import io.swagger.annotations.ApiParam;
import liuyuyang.net.dto.PageDTO;
import lombok.Data;

@Data
public class CateFilterDTO extends PageDTO {
    @ApiParam(value = "展示模式：默认 tree 结构，list 展示列表结构")
    String pattern;
}
