package liuyuyang.net.vo.operationlog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OperationLogVO {
    @Schema(description = "日志ID", example = "1")
    private Integer id;

    @Schema(description = "操作模块", example = "文章管理")
    private String module;

    @Schema(description = "操作类型：新增/修改/删除/登录/操作", example = "新增")
    private String type;

    @Schema(description = "操作描述", example = "新增文章")
    private String description;

    @Schema(description = "操作人", example = "admin")
    private String username;

    @Schema(description = "请求方式", example = "POST")
    private String method;

    @Schema(description = "请求地址", example = "/api/article")
    private String url;

    @Schema(description = "操作IP", example = "127.0.0.1")
    private String ip;

    @Schema(description = "请求参数（JSON，敏感字段已脱敏）")
    private String params;

    @Schema(description = "操作状态：1 成功，0 失败", example = "1")
    private Integer status;

    @Schema(description = "失败时的错误信息")
    private String errorMsg;

    @Schema(description = "耗时（毫秒）", example = "35")
    private Integer elapsed;

    @Schema(description = "操作时间（毫秒时间戳）", example = "1723533206613")
    private Long createTime;
}
