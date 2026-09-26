package liuyuyang.net.dto.operationlog;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.FilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogFilterDTO extends FilterDTO {
    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作类型")
    private String type;

    @Schema(description = "操作状态：1 成功，0 失败")
    private Integer status;

    @Schema(description = "关键词（模糊匹配操作描述/请求地址/操作人/IP）")
    private String keyword;
}
