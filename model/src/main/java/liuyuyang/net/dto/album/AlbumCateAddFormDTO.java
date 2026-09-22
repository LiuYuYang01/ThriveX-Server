package liuyuyang.net.dto.album;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AlbumCateAddFormDTO {
    @Schema(description = "相册名称", example = "旅行", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "相册封面", example = "http://123.com/images/example.jpg")
    private String cover;

    @TableField(exist = false)
    @Schema(description = "相册的照片数量", example = "10")
    private Integer count;
}
