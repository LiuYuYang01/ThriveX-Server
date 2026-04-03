package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class Comment extends BaseModel {
    @ApiModelProperty(value = "评论者名称", example = "宇阳", required = true)
    private String name;

    @ApiModelProperty(value = "评论者头像", example = "yuyang.jpg")
    private String avatar;

    @ApiModelProperty(value = "评论者邮箱", example = "liuyuyang1024@yeah.net")
    private String email;

    @ApiModelProperty(value = "评论者网站", example = "https://blog.liuyuyang.net")
    private String url;

    @ApiModelProperty(value = "评论内容", example = "这是一段评论内容", required = true)
    private String content;

    @ApiModelProperty(value = "文章ID", example = "1", required = true)
    private Integer articleId;

    @ApiModelProperty(value = "父评论ID，一级评论为 0", example = "0", required = true)
    private Integer commentId;

    @ApiModelProperty(value = "审核状态：0 待审核（默认），1 审核通过", example = "1")
    private Integer auditStatus;
}
