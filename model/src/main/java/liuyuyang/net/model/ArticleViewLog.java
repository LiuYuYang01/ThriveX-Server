package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("article_view_log")
@EqualsAndHashCode(callSuper = true)
public class ArticleViewLog extends BaseModel {
    @Schema(description = "文章ID")
    private Integer articleId;

    @Schema(description = "访客IP")
    private String ip;
}
