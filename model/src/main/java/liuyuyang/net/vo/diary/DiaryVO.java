package liuyuyang.net.vo.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DiaryVO {
    @Schema(description = "日记ID", example = "1")
    private Integer id;

    @Schema(description = "日记日期", example = "2026-10-23")
    private String diaryDate;

    @Schema(description = "标题", example = "关于一场没下的雨")
    private String title;

    @Schema(description = "正文")
    private String content;

    @Schema(description = "图片地址列表")
    private List<String> images;

    @Schema(description = "心情", example = "🌿")
    private String mood;

    @Schema(description = "天气", example = "rainy")
    private String weather;

    @Schema(description = "位置", example = "厦门市 · 环岛路")
    private String location;

    @Schema(description = "创建时间", example = "1783153776358")
    private Long createTime;

    @Schema(description = "更新时间", example = "1783153776358")
    private Long updateTime;
}
