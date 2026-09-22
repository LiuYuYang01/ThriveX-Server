package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("article_tag")
public class ArticleTag {
    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "文章ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer articleId;

    @Schema(description = "标签ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer tagId;
}
