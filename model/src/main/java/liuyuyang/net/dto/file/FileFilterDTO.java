package liuyuyang.net.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "FileFilterDTO", description = "文件列表筛选与分页")
public class FileFilterDTO extends PageDTO {
    @Schema(description = "业务相对目录（如 article），将作为存储 key 前缀下的子路径", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dir;
}
