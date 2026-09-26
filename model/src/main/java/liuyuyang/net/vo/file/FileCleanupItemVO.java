package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileCleanupItemVO", description = "未引用清理的候选文件")
public class FileCleanupItemVO {
    @Schema(description = "文件名")
    private String name;

    @Schema(description = "业务相对目录")
    private String dir;

    @Schema(description = "对象 key（存储路径）")
    private String path;

    @Schema(description = "公开访问 URL")
    private String url;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "上传时间（毫秒时间戳）")
    private Long date;
}
