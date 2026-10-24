package liuyuyang.net.vo.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryMonthVO {
    @Schema(description = "年份", example = "2026")
    private Integer year;

    @Schema(description = "月份（1-12）", example = "10")
    private Integer month;

    @Schema(description = "该月日记篇数", example = "7")
    private Integer count;
}
