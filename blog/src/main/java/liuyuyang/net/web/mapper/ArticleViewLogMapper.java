package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.ArticleViewLog;
import liuyuyang.net.vo.analysis.HotArticleVO;
import liuyuyang.net.vo.analysis.ViewTrendItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleViewLogMapper extends BaseMapper<ArticleViewLog> {
    // 统计周期内热门文章：周期浏览量来自浏览日志，总量字段取自文章表
    // 文章可见性过滤下推到 SQL：无配置可见，草稿/删除/全站隐藏不可见
    @Select("select a.id, a.title, a.cover, a.view, a.like_count as likeCount, count(l.id) as recentViews "
            + "from article a "
            + "left join article_config c on c.article_id = a.id "
            + "left join article_view_log l on l.article_id = a.id and l.create_time >= #{startTime} "
            + "where coalesce(c.is_draft, 0) = 0 and coalesce(c.is_del, 0) = 0 and coalesce(c.status, 1) <> 3 "
            + "group by a.id, a.title, a.cover, a.view, a.like_count "
            + "order by recentViews desc, a.view desc "
            + "limit #{limit}")
    List<HotArticleVO> selectHotArticles(@Param("startTime") Long startTime, @Param("limit") Integer limit);

    // 单篇文章按天浏览趋势
    @Select("select from_unixtime(create_time / 1000, '%Y-%m-%d') as date, count(*) as count "
            + "from article_view_log "
            + "where article_id = #{articleId} and create_time >= #{startTime} "
            + "group by date order by date")
    List<ViewTrendItemVO> selectViewTrend(@Param("articleId") Integer articleId, @Param("startTime") Long startTime);
}
