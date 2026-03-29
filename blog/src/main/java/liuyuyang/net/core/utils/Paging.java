package liuyuyang.net.core.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.result.IPage;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Paging {
    // 将分页数据转换为强类型分页结果
    public static <T> IPage<T> of(Page<T> data) {
        return new IPage<>(
                data.getCurrent(),
                data.getPageSize(),
                data.getPages(),
                data.getCurrent() > 1,
                data.getCurrent() < data.getPages(),
                data.getTotal(),
                data.getRecords()
        );
    }

    // 将分页数据过滤为指定格式
    public static <T> Map<String, Object> filter(Page<T> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("page", data.getCurrent()); // 当前页
        result.put("size", data.getPageSize()); // 每页数量
        result.put("pages", data.getPages()); // 总页数
        result.put("prev", data.getCurrent() > 1); // 是否还有上一页
        result.put("next", data.getCurrent() < data.getPages()); // 是否还有下一页
        result.put("total", data.getTotal()); // 总数量
        result.put("result", data.getRecords()); // 数据

        return result;
    }
}
