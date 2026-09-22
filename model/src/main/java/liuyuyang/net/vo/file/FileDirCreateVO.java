package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileDirCreateVO", description = "创建逻辑目录结果")
public class FileDirCreateVO {
    @Schema(description = "规范化后的目录前缀")
    private String dir;

    @Schema(description = "占位对象 key")
    private String placeholder;

    @Schema(description = "新建目录节点，便于前端本地插入树")
    private FileTreeNodeVO node;
}
