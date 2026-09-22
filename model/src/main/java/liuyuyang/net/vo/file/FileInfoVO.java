package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FileInfoVO", description = "单个文件元信息")
public class FileInfoVO {
    @Schema(description = "文件名")
    private String name;

    @Schema(description = "对象 key（存储路径）")
    private String path;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "七牛 hash")
    private String hash;

    @Schema(description = "MIME 类型")
    private String mimeType;

    @Schema(description = "上传时间（毫秒时间戳）")
    private Long putTime;

    @Schema(description = "公开访问 URL")
    private String url;
}
