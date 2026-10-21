package liuyuyang.net.web.mapper;

import liuyuyang.net.vo.search.SearchItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索复杂查询见 resources/mapper/SearchMapper.xml（共用可见性过滤片段 visibleArticleFrom）
 */
@Mapper
public interface SearchMapper {

    // ngram 全文索引查询（关键词长度 >= 2 时使用）
    List<SearchItemVO> searchArticles(@Param("kw") String kw, @Param("limit") Integer limit);

    List<SearchItemVO> searchRecords(@Param("kw") String kw, @Param("limit") Integer limit);

    // 短词回退：ngram 最小 token 为 2，单字关键词退化为 LIKE
    List<SearchItemVO> searchArticlesLike(@Param("kw") String kw, @Param("limit") Integer limit);

    List<SearchItemVO> searchRecordsLike(@Param("kw") String kw, @Param("limit") Integer limit);
}
