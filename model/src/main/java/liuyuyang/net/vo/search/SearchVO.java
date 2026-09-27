package liuyuyang.net.vo.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchVO {
    private List<SearchItemVO> articles;
    private List<SearchItemVO> records;
}
