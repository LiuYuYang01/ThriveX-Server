package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileListItemVO", description = "目录下列表中的单条文件")
public class FileListItemVO {
    @Schema(description = "域名根路径前缀")
    private String basePath;

    @Schema(description = "业务相对目录")
    private String dir;

    @Schema(description = "对象 key（存储路径）")
    private String path;

    @Schema(description = "文件名")
    private String name;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "扩展名（小写）")
    private String type;

    @Schema(description = "上传时间（毫秒时间戳）")
    private Long date;

    @Schema(description = "公开访问 URL")
    private String url;
}
