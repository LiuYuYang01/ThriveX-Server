package liuyuyang.net.vo.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SearchItemVO {
    private Integer id;
    // 文章标题
    private String title;
    // 闪念内容片段（服务端截取命中关键词的上下文）
    private String snippet;
    // 闪念原始内容，仅用于服务端截取 snippet，不返回给前端
    @JsonIgnore
    private String content;
}
