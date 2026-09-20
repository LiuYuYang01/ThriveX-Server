package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileDirDeleteVO", description = "删除逻辑目录结果")
public class FileDirDeleteVO {
    @Schema(description = "被删除的目录前缀")
    private String dir;

    @Schema(description = "删除的对象数量")
    private Integer deleted;
}
