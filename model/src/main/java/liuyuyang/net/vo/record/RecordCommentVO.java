package liuyuyang.net.vo.record;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.model.RecordComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordCommentVO extends RecordComment {
    @Schema(description = "说说内容摘要")
    @TableField(exist = false)
    private String recordContent;

    @Schema(description = "被回复者昵称（二级评论展示用）")
    @TableField(exist = false)
    private String replyName;

    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<RecordCommentVO> children = new ArrayList<>();
}
