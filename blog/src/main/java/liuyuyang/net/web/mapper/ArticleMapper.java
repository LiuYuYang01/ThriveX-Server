package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Article;
import liuyuyang.net.vo.analysis.SiteSummaryVO;
import liuyuyang.net.vo.analysis.ViewTrendItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    // 站点内容概览：可见文章的累计篇数/字数/浏览/获赞（可见性过滤与热门文章一致）
    @Select("select count(*) as articleCount, coalesce(sum(char_length(a.content)), 0) as totalWords, "
            + "coalesce(sum(a.view), 0) as totalViews, coalesce(sum(a.like_count), 0) as totalLikes "
            + "from article a "
            + "left join article_config c on c.article_id = a.id "
            + "where coalesce(c.is_draft, 0) = 0 and coalesce(c.is_del, 0) = 0 and coalesce(c.status, 1) <> 3")
    SiteSummaryVO selectSiteSummary();
}
