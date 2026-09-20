package liuyuyang.net.dto.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AlbumImageAddFormDTO {
    @Schema(description = "相册名称", example = "旅行", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "相册介绍", example = "青春没有售价，泰山就在脚下")
    private String description;

    @Schema(description = "相册地址", example = "http://123.com/images/example.jpg")
    private String image;

    @Schema(description = "相册ID", example = "1")
    private Integer cateId;

    @Schema(description = "创建时间", example = "1723533206613", requiredMode = Schema.RequiredMode.REQUIRED)
    private String createTime;
}