package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("search_log")
@EqualsAndHashCode(callSuper = true)
public class SearchLog extends BaseModel {
    @Schema(description = "搜索关键词")
    private String keyword;
}
