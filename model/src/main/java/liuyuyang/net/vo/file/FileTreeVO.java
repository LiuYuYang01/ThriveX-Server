package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "FileTreeVO", description = "整桶文件目录树")
public class FileTreeVO {
    @Schema(description = "域名根路径前缀")
    private String basePath;

    @Schema(description = "列举到的原始对象条数（含占位对象）")
    private Integer total;

    @Schema(description = "一级目录树根节点列表")
    private List<FileTreeNodeVO> result;
}
