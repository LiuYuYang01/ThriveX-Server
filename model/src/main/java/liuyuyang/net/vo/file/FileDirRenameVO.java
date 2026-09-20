package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileDirRenameVO", description = "重命名逻辑目录结果")
public class FileDirRenameVO {
    @Schema(description = "原目录前缀")
    private String fromDir;

    @Schema(description = "新目录前缀")
    private String toDir;

    @Schema(description = "移动的对象数量")
    private Integer moved;
}
