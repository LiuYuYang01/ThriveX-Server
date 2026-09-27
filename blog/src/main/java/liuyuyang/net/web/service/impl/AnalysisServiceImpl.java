package liuyuyang.net.web.service.impl;

import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.model.Article;
import liuyuyang.net.vo.analysis.HotArticleVO;
import liuyuyang.net.vo.analysis.HotKeywordVO;
import liuyuyang.net.vo.analysis.ViewTrendItemVO;
import liuyuyang.net.web.mapper.ArticleMapper;
import liuyuyang.net.web.mapper.ArticleViewLogMapper;
import liuyuyang.net.web.mapper.SearchLogMapper;
import liuyuyang.net.web.service.AnalysisService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalysisServiceImpl implements AnalysisService {    private static final int DEFAULT_DAYS = 30;
    private static final int MAX_DAYS = 365;
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;
    private static final long DAY_MILLIS = 24L * 3600 * 1000;

    @Resource
    private ArticleViewLogMapper articleViewLogMapper;
    @Resource
    private SearchLogMapper searchLogMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public List<HotArticleVO> getHotArticles(Integer days, Integer limit) {
        long startTime = System.currentTimeMillis() - normalizeDays(days) * DAY_MILLIS;
        return articleViewLogMapper.selectHotArticles(startTime, normalizeLimit(limit));
    }

    @Override
    public List<HotKeywordVO> getHotKeywords(Integer days, Integer limit) {
        long startTime = System.currentTimeMillis() - normalizeDays(days) * DAY_MILLIS;
        return searchLogMapper.selectHotKeywords(startTime, normalizeLimit(limit));
    }

    @Override
    public List<ViewTrendItemVO> getArticleViewTrend(Integer articleId, Integer days) {
        if (articleId == null || articleMapper.selectById(articleId) == null) {
            throw new CustomException("文章不存在");
        }

        int day = normalizeDays(days);
        long startTime = System.currentTimeMillis() - day * DAY_MILLIS;

        Map<String, Integer> countByDate = articleViewLogMapper.selectViewTrend(articleId, startTime).stream()
                .collect(Collectors.toMap(ViewTrendItemVO::getDate, ViewTrendItemVO::getCount, (a, b) -> a));

        // 日志里没有浏览的日期补 0，前端拿到的曲线是连续的
        List<ViewTrendItemVO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = day - 1; i >= 0; i--) {
            String date = today.minusDays(i).toString();
            result.add(new ViewTrendItemVO(date, countByDate.getOrDefault(date, 0)));
        }
        return result;
    }

    private int normalizeDays(Integer days) {
        if (days == null || days < 1) return DEFAULT_DAYS;
        return Math.min(days, MAX_DAYS);
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) return DEFAULT_LIMIT;
        return Math.min(limit, MAX_LIMIT);
    }
}
