package liuyuyang.net.vo.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 密语问答的对外视图：只有问题，永不包含答案或其哈希。
 */
@Data
public class DiaryQuestionVO {
    @Schema(description = "问题ID", example = "1")
    private Integer id;

    @Schema(description = "问题", example = "我们第一次见面是在哪条路？")
    private String question;

    @Schema(description = "创建时间（仅管理端返回）", example = "1783153776358")
    private Long createTime;
}
