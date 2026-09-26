package liuyuyang.net.web.service;

import liuyuyang.net.vo.analysis.HotArticleVO;
import liuyuyang.net.vo.analysis.HotKeywordVO;
import liuyuyang.net.vo.analysis.ViewTrendItemVO;

import java.util.List;

public interface AnalysisService {
    /**
     * 热门文章排行（基于自建浏览日志）
     *
     * @param days  统计周期（天）
     * @param limit 返回条数
     */
    List<HotArticleVO> getHotArticles(Integer days, Integer limit);

    /**
     * 站内搜索热词
     *
     * @param days  统计周期（天）
     * @param limit 返回条数
     */
    List<HotKeywordVO> getHotKeywords(Integer days, Integer limit);

    /**
     * 单篇文章按天浏览趋势，无浏览的日期补 0
     *
     * @param articleId 文章ID
     * @param days      统计周期（天）
     */
    List<ViewTrendItemVO> getArticleViewTrend(Integer articleId, Integer days);
}
