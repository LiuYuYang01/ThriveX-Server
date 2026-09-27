package liuyuyang.net.web.mapper;

import liuyuyang.net.vo.search.SearchItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchMapper {
    // 文章可见性过滤下推到 SQL：无配置可见，草稿/删除/全站隐藏不可见
    String ARTICLE_FROM = "from article a left join article_config c on c.article_id = a.id "
            + "where coalesce(c.is_draft, 0) = 0 and coalesce(c.is_del, 0) = 0 and coalesce(c.status, 1) <> 3";

    // ngram 全文索引查询（关键词长度 >= 2 时使用）
    @Select("select a.id, a.title " + ARTICLE_FROM
            + " and match(a.title) against(#{kw} in boolean mode) "
            + "order by a.create_time desc limit #{limit}")
    List<SearchItemVO> searchArticles(@Param("kw") String kw, @Param("limit") Integer limit);

    @Select("select id, content from record "
            + "where match(content) against(#{kw} in boolean mode) "
            + "order by create_time desc limit #{limit}")
    List<SearchItemVO> searchRecords(@Param("kw") String kw, @Param("limit") Integer limit);

    // 短词回退：ngram 最小 token 为 2，单字关键词退化为 LIKE
    @Select("select a.id, a.title " + ARTICLE_FROM
            + " and a.title like concat('%', #{kw}, '%') "
            + "order by a.create_time desc limit #{limit}")
    List<SearchItemVO> searchArticlesLike(@Param("kw") String kw, @Param("limit") Integer limit);

    @Select("select id, content from record "
            + "where content like concat('%', #{kw}, '%') "
            + "order by create_time desc limit #{limit}")
    List<SearchItemVO> searchRecordsLike(@Param("kw") String kw, @Param("limit") Integer limit);
}
