package liuyuyang.net.vo.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 月份书册：一次返回某个月的日记（按日期正序），以及全年月索引的篇数分布。
 */
@Data
public class DiaryBookVO {
    @Schema(description = "当前翻阅的年份", example = "2026")
    private Integer year;

    @Schema(description = "当前翻阅的月份（1-12）", example = "10")
    private Integer month;

    @Schema(description = "日记总篇数", example = "23")
    private Integer total;

    @Schema(description = "有日记的月份索引，按月倒序")
    private List<DiaryMonthVO> months;

    @Schema(description = "当月日记，按日期正序")
    private List<DiaryVO> entries;
}
