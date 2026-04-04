package liuyuyang.net.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IPage<T> {
    private Long page; // 当前页
    private Long size; // 每页数量
    private Long pages; // 总页数
    private Boolean prev; // 是否还有上一页
    private Boolean next; // 是否还有下一页
    private Long total; // 总数量
    private List<T> result; // 数据
}
