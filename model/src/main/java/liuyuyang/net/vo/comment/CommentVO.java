package liuyuyang.net.vo.comment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVO extends Comment {
    @ApiModelProperty(value = "文章标题")
    @TableField(exist = false)
    private String articleTitle;

    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommentVO> children = new ArrayList<>();
}
