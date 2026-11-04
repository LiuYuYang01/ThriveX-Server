package liuyuyang.net.vo.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 日记归档目录：年 → 月。
 */
@Data
public class DiaryArchiveVO {
    @Schema(description = "年份", example = "2026")
    private Integer year;

    @Schema(description = "该年日记篇数")
    private Long total;

    @Schema(description = "该年各月")
    private List<Month> months = new ArrayList<>();

    @Data
    public static class Month {
        @Schema(description = "月份（1-12）", example = "11")
        private Integer month;

        @Schema(description = "月份名称", example = "十一月")
        private String name;

        @Schema(description = "该月日记篇数")
        private Long total;
    }
}
