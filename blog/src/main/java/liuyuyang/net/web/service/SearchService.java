package liuyuyang.net.web.service;

import liuyuyang.net.vo.search.SearchVO;

public interface SearchService {
    SearchVO search(String keyword, Integer limit);
}
