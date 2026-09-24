package liuyuyang.net.web.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import liuyuyang.net.vo.search.SearchItemVO;
import liuyuyang.net.vo.search.SearchVO;
import liuyuyang.net.web.mapper.SearchMapper;
import liuyuyang.net.web.service.SearchService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;
    // 片段在命中位置前保留的字符数
    private static final int SNIPPET_BEFORE = 20;
    // 片段在命中位置后保留的字符数
    private static final int SNIPPET_AFTER = 60;

    // 搜索结果与登录态无关，短 TTL 缓存兜住重复搜索热门词
    private final Cache<String, SearchVO> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(60))
            .maximumSize(500)
            .build();

    @Resource
    private SearchMapper searchMapper;

    @Override
    public SearchVO search(String keyword, Integer limit) {
        String kw = keyword == null ? "" : keyword.trim();
        int size = limit == null || limit < 1 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);
        if (kw.isEmpty()) {
            return emptyResult();
        }
        return cache.get(kw + ":" + size, k -> doSearch(kw, size));
    }

    private SearchVO doSearch(String kw, int size) {
        SearchVO vo = new SearchVO();

        // ngram 最小 token 为 2：单字关键词 FULLTEXT 匹配不到，退化为 LIKE
        if (kw.length() < 2) {
            vo.setArticles(searchMapper.searchArticlesLike(kw, size));
            vo.setRecords(buildRecordItems(searchMapper.searchRecordsLike(kw, size), kw));
            return vo;
        }

        // 剥掉 boolean mode 操作符后按短语匹配（配合 ngram 约等于子串搜索）
        String boolKw = "\"" + kw.replaceAll("[\"+\\-><()~*@]", "") + "\"";
        if (boolKw.length() <= 2) {
            return emptyResult();
        }

        vo.setArticles(searchMapper.searchArticles(boolKw, size));
        vo.setRecords(buildRecordItems(searchMapper.searchRecords(boolKw, size), kw));
        return vo;
    }

    // 闪念只返回 id + 内容片段，content 中的原文不外发
    private List<SearchItemVO> buildRecordItems(List<SearchItemVO> list, String kw) {
        if (list == null) return Collections.emptyList();
        for (SearchItemVO item : list) {
            item.setSnippet(buildSnippet(item.getContent(), kw));
        }
        return list;
    }

    private String buildSnippet(String content, String kw) {
        if (content == null) return "";
        // 压缩连续空白，避免片段里出现换行
        String text = content.replaceAll("\\s+", " ").trim();
        int idx = text.indexOf(kw);
        if (idx < 0) return text.length() > 80 ? text.substring(0, 80) + "…" : text;

        int start = Math.max(0, idx - SNIPPET_BEFORE);
        int end = Math.min(text.length(), idx + kw.length() + SNIPPET_AFTER);
        return (start > 0 ? "…" : "") + text.substring(start, end) + (end < text.length() ? "…" : "");
    }

    private SearchVO emptyResult() {
        SearchVO vo = new SearchVO();
        vo.setArticles(Collections.emptyList());
        vo.setRecords(Collections.emptyList());
        return vo;
    }
}
