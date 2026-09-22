package liuyuyang.net.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class File {
    @Schema(description = "文件名称", example = "123.txt")
    private String name;
    @Schema(description = "文件大小", example = "17240424901572397")
    private Long size;
    @Schema(description = "文件类型", example = "image/png")
    private String type;
    @Schema(description = "文件地址", example = "http://xxx.com/1.jpg")
    private String url;
    @Schema(description = "文件上传时间", example = "1723533206613")
    private Long createTime;
}
