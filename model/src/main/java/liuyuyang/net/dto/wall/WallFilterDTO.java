package liuyuyang.net.dto.wall;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.enums.wall.WallAuditStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WallFilterDTO extends FilterDTO {
    @Schema(description = "根据留言内容模糊查询")
    private String content;

    @Schema(description = "根据分类进行筛选")
    private Integer cateId;

    @Schema(description = "筛选设为精选的留言")
    private Integer isChoice;

    @Schema(description = "0表示获取待审核的留言 | 1表示获取审核通过的留言（默认）")
    private WallAuditStatusEnum status = WallAuditStatusEnum.APPROVED;
}
