package liuyuyang.net.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Rss {
    @Schema(description = "作者", example = "宇阳")
    private String author;
    @Schema(description = "网站图片", example = "http://127.0.0.1:5000/1.jpg")
    private String image;
    @Schema(description = "网站邮箱", example = "liuyuyang1024@yeah.net")
    private String email;
    @Schema(description = "网站类型", example = "技术类")
    private String type;
    @Schema(description = "网站标题", example = "这是一个网站")
    private String title;
    @Schema(description = "网站描述", example = "这是一个网站的描述")
    private String description;
    @Schema(description = "网站链接", example = "/")
    private String url;
    @Schema(description = "网站创建时间", example = "1723533206613")
    private Long createTime;
}
