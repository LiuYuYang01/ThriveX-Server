package liuyuyang.net.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewTrendItemVO {
    @Schema(description = "日期，格式 YYYY-MM-DD")
    private String date;

    @Schema(description = "浏览量")
    private Integer count;
}
