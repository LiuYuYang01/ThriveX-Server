package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileTreeFileVO", description = "文件树中的文件节点")
public class FileTreeFileVO {
    @Schema(description = "节点类型：file")
    private String type;

    @Schema(description = "对象 key（存储路径）")
    private String path;

    @Schema(description = "域名根路径前缀")
    private String basePath;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "文件名")
    private String name;

    @Schema(description = "父级目录 key")
    private String dir;

    @Schema(description = "扩展名（小写）")
    private String ext;

    @Schema(description = "上传时间（毫秒时间戳）")
    private Long date;

    @Schema(description = "公开访问 URL")
    private String url;
}
