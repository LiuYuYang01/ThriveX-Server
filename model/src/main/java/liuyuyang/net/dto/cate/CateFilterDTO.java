package liuyuyang.net.dto.cate;

import io.swagger.annotations.ApiParam;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.enums.cate.CatePatternEnum;
import lombok.Data;

@Data
public class CateFilterDTO extends PageDTO {
    @ApiParam(value = "展示模式：tree 树形结构（默认），list 列表结构")
    private CatePatternEnum pattern;
}
