package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import liuyuyang.net.enums.article.ArticleStatusEnum;
import liuyuyang.net.model.Article;
import liuyuyang.net.model.ArticleConfig;
import liuyuyang.net.model.WebConfig;
import liuyuyang.net.vo.seo.SeoArticleCheckVO;
import liuyuyang.net.vo.seo.SeoArticleIssueVO;
import liuyuyang.net.vo.seo.SeoLinkArticleVO;
import liuyuyang.net.vo.seo.SeoLinkCheckVO;
import liuyuyang.net.vo.seo.SeoLinkResultVO;
import liuyuyang.net.vo.seo.SeoSitemapCheckVO;
import liuyuyang.net.web.mapper.ArticleConfigMapper;
import liuyuyang.net.web.mapper.ArticleMapper;
import liuyuyang.net.web.service.SeoService;
import liuyuyang.net.web.service.WebConfigService;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SEO 体检服务：文章元信息检查、sitemap 生成检查、正文死链检测
 *
 * 体检范围（元信息 + 死链）= 非草稿且未删除的文章，与后台文章管理列表一致；
 * sitemap 对比范围在此基础上排除全站隐藏文章（与博客端 sitemap 生成规则一致）。
 *
 * URL 校验说明：这里的请求目标全部来自管理员配置的站点地址和文章内容，
 * 且接口需要登录才能调用，因此只校验协议与域名格式，允许内网/本地地址
 * （支持博客与服务端同机自托管的部署方式），不做 SSRF 内网拦截。
 */
@Slf4j
@Service
public class SeoServiceImpl implements SeoService {
    private static final String USER_AGENT = "Mozilla/5.0 (compatible; ThriveX-SEO/1.0)";
    // 死链检测：单次最多检测的链接数，超出部分通过 truncated 标记
    private static final int MAX_LINKS = 200;
    private static final int LINK_CONCURRENCY = 20;
    private static final int CONNECT_TIMEOUT = 4000;
    private static final int READ_TIMEOUT = 5000;
    private static final int MAX_REDIRECTS = 4;
    // 部分站点对 HEAD 请求返回异常状态码，需要改用 GET 重试
    private static final Set<Integer> HEAD_RETRY_STATUS = Set.of(400, 403, 405, 501);
    // 与博客端 sitemap.ts 的静态页面保持一致（"" 为首页）
    private static final List<String> STATIC_PAGES = List.of("", "/tags", "/album", "/friend", "/sponsors", "/wall", "/record");

    private static final Pattern MD_LINK = Pattern.compile("!?\\[[^\\]]*\\]\\(\\s*([^)\\s]+)(?:\\s+\"[^\"]*\")?\\s*\\)");
    private static final Pattern AUTO_LINK = Pattern.compile("<(https?://[^>\\s]+)>");
    private static final Pattern HTML_HREF = Pattern.compile("(?i)<a\\s[^>]*href\\s*=\\s*([\"'])([^\"']*)\\1");
    private static final Pattern HTML_SRC = Pattern.compile("(?i)<img\\s[^>]*src\\s*=\\s*([\"'])([^\"']*)\\1");
    private static final Pattern LOC_TAG = Pattern.compile("(?s)<loc[^>]*>\\s*(.*?)\\s*</loc>");
    private static final Pattern SITEMAP_ARTICLE = Pattern.compile(".*/article/\\d+$");

    private final ExecutorService executor = Executors.newFixedThreadPool(LINK_CONCURRENCY);

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleConfigMapper articleConfigMapper;
    @Resource
    private WebConfigService webConfigService;

    @Override
    public SeoArticleCheckVO checkArticleMeta() {
        List<Article> articles = getPublishedArticles(false);

        SeoArticleCheckVO vo = new SeoArticleCheckVO();
        vo.setTotal(articles.size());
        int missingDescription = 0;
        int missingCover = 0;

        for (Article article : articles) {
            boolean noDescription = isBlank(article.getDescription());
            boolean noCover = isBlank(article.getCover());
            if (!noDescription && !noCover) continue;

            if (noDescription) missingDescription++;
            if (noCover) missingCover++;

            SeoArticleIssueVO issue = new SeoArticleIssueVO();
            issue.setId(article.getId());
            issue.setTitle(article.getTitle());
            issue.setMissingDescription(noDescription);
            issue.setMissingCover(noCover);
            vo.getArticles().add(issue);
        }

        vo.setMissingDescriptionTotal(missingDescription);
        vo.setMissingCoverTotal(missingCover);
        return vo;
    }

    @Override
    public SeoSitemapCheckVO checkSitemap() {
        SeoSitemapCheckVO vo = new SeoSitemapCheckVO();
        String siteUrl = getSiteUrl();
        if (isBlank(siteUrl)) {
            vo.setReachable(false);
            vo.setMessage("请先在网站配置中填写网站地址（url）");
            return vo;
        }

        // 去掉尾部斜杠，避免拼接出 //article/1 这类双斜杠地址
        siteUrl = siteUrl.replaceAll("/+$", "");
        String sitemapUrl = siteUrl + "/sitemap.xml";
        vo.setUrl(sitemapUrl);

        List<String> locs;
        try {
            locs = fetchSitemapLocs(sitemapUrl);
        } catch (Exception e) {
            log.warn("sitemap 检查失败: {} - {}", sitemapUrl, e.getMessage());
            vo.setReachable(false);
            vo.setMessage("sitemap 无法访问：" + e.getMessage());
            return vo;
        }

        Set<String> normalized = locs.stream().map(this::normalizeUrl).collect(Collectors.toSet());

        vo.setReachable(true);
        vo.setSitemapTotal(locs.size());

        // 公开文章（非草稿、未删除、非全站隐藏）应被 sitemap 收录
        List<Article> publicArticles = getPublishedArticles(true);
        vo.setArticleTotal(publicArticles.size());

        int sitemapArticleCount = 0;
        for (String loc : normalized) {
            if (SITEMAP_ARTICLE.matcher(loc).matches()) sitemapArticleCount++;
        }
        vo.setSitemapArticleCount(sitemapArticleCount);

        for (Article article : publicArticles) {
            String expected = normalizeUrl(siteUrl + "/article/" + article.getId());
            if (normalized.contains(expected)) continue;

            SeoArticleIssueVO issue = new SeoArticleIssueVO();
            issue.setId(article.getId());
            issue.setTitle(article.getTitle());
            issue.setMissingDescription(false);
            issue.setMissingCover(false);
            vo.getMissingArticles().add(issue);
        }

        for (String path : STATIC_PAGES) {
            String expected = normalizeUrl(siteUrl + path);
            if (!normalized.contains(expected)) vo.getMissingStaticPages().add(path.isEmpty() ? "/" : path);
        }

        return vo;
    }

    @Override
    public SeoLinkCheckVO checkLinks() {
        List<Article> articles = getPublishedArticles(false);

        // 提取所有文章正文中的外链，同一链接只检测一次（LinkedHashMap 保持出现顺序）
        Map<String, List<SeoLinkArticleVO>> linkMap = new LinkedHashMap<>();
        for (Article article : articles) {
            for (String url : extractLinks(article.getContent())) {
                linkMap.computeIfAbsent(url, k -> new ArrayList<>())
                        .add(buildLinkArticle(article));
            }
        }

        SeoLinkCheckVO vo = new SeoLinkCheckVO();
        vo.setArticleTotal(articles.size());
        vo.setLinkTotal(linkMap.size());
        vo.setTruncated(linkMap.size() > MAX_LINKS);

        List<String> toCheck = linkMap.keySet().stream().limit(MAX_LINKS).collect(Collectors.toList());

        Map<String, SeoLinkResultVO> resultMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = toCheck.stream()
                .map(url -> CompletableFuture.runAsync(() -> {
                    SeoLinkResultVO result = probe(url);
                    result.setArticles(linkMap.get(url));
                    resultMap.put(url, result);
                }, executor))
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 死链排前面，同组内保持出现顺序
        List<SeoLinkResultVO> links = toCheck.stream().map(resultMap::get)
                .sorted(Comparator.comparingInt(r -> Boolean.TRUE.equals(r.getOk()) ? 1 : 0))
                .collect(Collectors.toList());

        vo.setLinks(links);
        vo.setCheckedTotal(links.size());
        vo.setBrokenTotal((int) links.stream().filter(r -> !Boolean.TRUE.equals(r.getOk())).count());
        return vo;
    }

    /**
     * 查询体检范围内的文章：非草稿且未删除；publicOnly 为 true 时再排除全站隐藏文章
     */
    private List<Article> getPublishedArticles(boolean publicOnly) {
        LambdaQueryWrapper<ArticleConfig> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(ArticleConfig::getIsDraft, false).eq(ArticleConfig::getIsDel, false);
        if (publicOnly) configWrapper.ne(ArticleConfig::getStatus, ArticleStatusEnum.HIDE);

        List<Integer> ids = articleConfigMapper.selectList(configWrapper)
                .stream()
                .map(ArticleConfig::getArticleId)
                .distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) return new ArrayList<>();

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Article::getId, ids);
        wrapper.orderByDesc(Article::getCreateTime);
        return articleMapper.selectList(wrapper);
    }

    /**
     * 从文章内容中提取可检测的 http(s) 链接（markdown 链接/图片、HTML a/img、自动链接）
     */
    private Set<String> extractLinks(String content) {
        Set<String> urls = new LinkedHashSet<>();
        if (isBlank(content)) return urls;

        collectMatches(urls, MD_LINK.matcher(content), 1);
        collectMatches(urls, AUTO_LINK.matcher(content), 1);
        collectMatches(urls, HTML_HREF.matcher(content), 2);
        collectMatches(urls, HTML_SRC.matcher(content), 2);

        return urls.stream()
                .filter(url -> {
                    String lower = url.toLowerCase(Locale.ROOT);
                    return (lower.startsWith("http://") || lower.startsWith("https://")) && url.length() <= 2048;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void collectMatches(Set<String> urls, Matcher matcher, int group) {
        while (matcher.find()) {
            String url = matcher.group(group);
            if (!isBlank(url)) urls.add(url.trim());
        }
    }

    /**
     * 检测单个链接：先 HEAD，被拒时改用 GET；重定向跟随在 open() 中处理
     */
    private SeoLinkResultVO probe(String url) {
        SeoLinkResultVO vo = new SeoLinkResultVO();
        vo.setUrl(url);
        vo.setOk(false);

        try {
            HttpURLConnection connection = open(url, "HEAD");
            int status = connection.getResponseCode();
            closeQuietly(connection);
            // 部分站点对 HEAD 请求返回异常状态码，改用 GET 重试
            if (HEAD_RETRY_STATUS.contains(status)) {
                connection = open(url, "GET");
                status = connection.getResponseCode();
                closeQuietly(connection);
            }

            vo.setStatus(status);
            boolean ok = status < 400;
            vo.setOk(ok);
            if (!ok) vo.setMessage(describeStatus(status));
            return vo;
        } catch (UnknownHostException e) {
            vo.setMessage("域名无法解析");
        } catch (SocketTimeoutException e) {
            vo.setMessage("请求超时");
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            vo.setMessage(message.length() > 120 ? message.substring(0, 120) : message);
        }
        return vo;
    }

    /**
     * 发起请求并手动跟随重定向（HttpURLConnection 默认不跟随 http→https 的跨协议跳转），
     * 返回最终响应的连接
     */
    private HttpURLConnection open(String url, String method) throws IOException {
        URI uri = parseUri(url);
        HttpURLConnection connection = openOnce(uri, method);

        for (int depth = 0; depth < MAX_REDIRECTS; depth++) {
            int status = connection.getResponseCode();
            if (status < 300 || status >= 400) return connection;

            String location = connection.getHeaderField("Location");
            connection.disconnect();
            if (isBlank(location)) throw new IOException("HTTP " + status + " 重定向缺少目标地址");

            uri = parseUri(uri.resolve(location.trim()).toString());
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!"http".equals(scheme) && !"https".equals(scheme)) throw new IOException("重定向到不支持的协议");
            connection = openOnce(uri, method);
        }
        throw new IOException("重定向次数过多");
    }

    private HttpURLConnection openOnce(URI uri, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "*/*");
        return connection;
    }

    private URI parseUri(String url) throws IOException {
        try {
            URI uri = new URI(url.trim());
            if (uri.getScheme() == null || uri.getHost() == null) throw new URISyntaxException(url, "缺少协议或域名");
            return uri;
        } catch (URISyntaxException e) {
            throw new IOException("URL 格式错误");
        }
    }

    private String describeStatus(int status) {
        if (status == 400) return "HTTP 400 请求无效";
        if (status == 401) return "HTTP 401 需要授权";
        if (status == 403) return "HTTP 403 被拒绝（可能拦截了自动请求）";
        if (status == 404) return "HTTP 404 页面不存在";
        if (status == 410) return "HTTP 410 内容已永久移除";
        if (status == 429) return "HTTP 429 请求过于频繁";
        if (status >= 500) return "HTTP " + status + " 服务器异常";
        return "HTTP " + status;
    }

    /**
     * 抓取 sitemap：自动识别 sitemap index，展开一层子 sitemap
     */
    private List<String> fetchSitemapLocs(String sitemapUrl) throws IOException {
        String xml = fetchText(sitemapUrl);
        List<String> locs = extractLocs(xml);

        if (xml.toLowerCase(Locale.ROOT).contains("<sitemapindex")) {
            // sitemap index 的 loc 是子 sitemap 地址
            List<String> children = locs.size() > 10 ? locs.subList(0, 10) : locs;
            List<String> merged = new ArrayList<>(locs);
            for (String child : children) {
                try {
                    merged.addAll(extractLocs(fetchText(child)));
                } catch (Exception e) {
                    log.warn("子 sitemap 抓取失败: {} - {}", child, e.getMessage());
                }
            }
            return merged;
        }
        return locs;
    }

    private List<String> extractLocs(String xml) {
        List<String> locs = new ArrayList<>();
        Matcher matcher = LOC_TAG.matcher(xml);
        while (matcher.find()) {
            String loc = unescapeXml(matcher.group(1)).trim();
            if (!isBlank(loc)) locs.add(loc);
        }
        return locs;
    }

    private String fetchText(String url) throws IOException {
        HttpURLConnection connection = open(url, "GET");
        int status = connection.getResponseCode();

        if (status >= 400) {
            closeQuietly(connection);
            throw new IOException("HTTP " + status);
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        try (InputStream in = connection.getInputStream()) {
            int len;
            while ((len = in.read(chunk)) != -1) buffer.write(chunk, 0, len);
        } finally {
            connection.disconnect();
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private void closeQuietly(HttpURLConnection connection) {
        try {
            InputStream stream = connection.getErrorStream();
            if (stream != null) stream.close();
        } catch (IOException ignored) {
        }
        connection.disconnect();
    }

    /**
     * 读取管理员配置的站点地址
     */
    private String getSiteUrl() {
        WebConfig config = webConfigService.getByName("web");
        if (config == null || config.getValue() == null) return "";
        Object url = config.getValue().get("url");
        return url != null ? String.valueOf(url).trim() : "";
    }

    /**
     * URL 归一化：主机不区分大小写、合并多余斜杠、去尾部斜杠，用于 sitemap 收录对比
     */
    private String normalizeUrl(String raw) {
        String url = raw.trim();
        int schemeEnd = url.indexOf("://");
        String scheme = schemeEnd >= 0 ? url.substring(0, schemeEnd).toLowerCase(Locale.ROOT) : "";
        String rest = schemeEnd >= 0 ? url.substring(schemeEnd + 3) : url;

        int pathStart = rest.indexOf('/');
        String host = (pathStart >= 0 ? rest.substring(0, pathStart) : rest).toLowerCase(Locale.ROOT);
        String path = pathStart >= 0 ? rest.substring(pathStart) : "";
        path = path.replaceAll("/{2,}", "/").replaceAll("/+$", "");

        return scheme + "://" + host + path;
    }

    private String unescapeXml(String value) {
        return value.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
    }

    private SeoLinkArticleVO buildLinkArticle(Article article) {
        SeoLinkArticleVO item = new SeoLinkArticleVO();
        item.setId(article.getId());
        item.setTitle(article.getTitle());
        return item;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
