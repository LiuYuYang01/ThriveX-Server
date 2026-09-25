package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.core.utils.UrlSecurityUtils;
import liuyuyang.net.web.mapper.LinkMapper;
import liuyuyang.net.web.mapper.LinkTypeMapper;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.Rss;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.service.RssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * RSS 聚合服务
 *
 * 缓存策略：缓存全量聚合结果（分页在缓存外进行），定时任务每小时抓取最新内容后
 * 直接写回缓存而不是清空缓存，配合启动预热，用户请求永远命中缓存，不会触发冷启动抓取。
 */
@Slf4j
@Service
public class RssServiceImpl implements RssService {
    private static final String CACHE_NAME = "rssCache";
    private static final String CACHE_KEY = "allFeeds";

    @Resource
    private LinkMapper linkMapper;
    @Resource
    private LinkTypeMapper linkTypeMapper;
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private CacheManager cacheManager;

    // 线程池，用于并发获取RSS内容
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // 类型缓存，避免重复查询数据库
    private final Map<Integer, String> typeCache = new ConcurrentHashMap<>();

    /**
     * 初始化方法，在Bean创建后自动执行
     * 预加载所有链接类型数据到内存缓存中
     */
    @PostConstruct
    public void init() {
        // 从数据库加载所有链接类型，并存入缓存
        linkTypeMapper.selectList(null).forEach(lt -> typeCache.put(lt.getId(), lt.getName()));
    }

    /**
     * 应用启动完成后预热缓存，避免首个访问的用户触发冷启动抓取
     * （ApplicationReadyEvent 触发时 Tomcat 已开始服务，期间未命中的请求会合并到本次预热抓取）
     */
    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void prewarmCache() {
        try {
            getAllFeeds();
            log.info("RSS 缓存预热完成");
        } catch (Exception e) {
            log.error("RSS 缓存预热失败: {}", e.getMessage());
        }
    }

    @Override
    public Page<Rss> getRssList(PageDTO pageDTO) {
        return commonUtils.paginate(pageDTO, getAllFeeds());
    }

    /**
     * 读取缓存的全量订阅列表
     * 未命中时通过 Caffeine 的 loader 加载，并发的未命中请求会合并到同一次抓取，避免缓存击穿
     */
    private List<Rss> getAllFeeds() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) return fetchAllFeeds();

        List<Rss> data = cache.get(CACHE_KEY, this::fetchAllFeeds);
        return data != null ? data : Collections.emptyList();
    }

    /**
     * 定时抓取最新内容并写回缓存：只替换、不失效，避免用户请求踩到缓存重建
     */
    @Override
    @Scheduled(fixedRate = 3600000) // 每小时更新一次
    public void refreshCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) return;

        try {
            cache.put(CACHE_KEY, fetchAllFeeds());
            log.info("RSS 缓存刷新完成");
        } catch (Exception e) {
            log.error("RSS 缓存刷新失败: {}", e.getMessage());
        }
    }

    /**
     * 抓取所有订阅源并按发布时间倒序返回（新的在前）
     */
    private List<Rss> fetchAllFeeds() {
        long start = System.currentTimeMillis();

        // 线程安全的列表，用于收集所有RSS条目
        List<Rss> rssList = Collections.synchronizedList(new ArrayList<>());

        // 仅聚合审核通过且配置了 rss 的链接
        List<Link> linkList = linkMapper
                .selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Link>()
                        .eq("status", 1)
                        .isNotNull("rss")
                        .ne("rss", ""));

        // 为每个有RSS地址的链接创建异步任务
        List<CompletableFuture<Void>> futures = linkList.stream()
                .filter(link -> link.getRss() != null) // 过滤掉没有RSS地址的链接
                .map(link -> CompletableFuture.runAsync(() -> processFeedWithTimeout(link, rssList), executorService)) // 异步处理每个RSS源
                .collect(Collectors.toList());

        // 等待所有异步任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 按发布时间降序排序后返回（新的在前）
        List<Rss> list = rssList.stream()
                .sorted(Comparator.comparingLong(Rss::getCreateTime).reversed())
                .collect(Collectors.toList());

        log.info("RSS 抓取完成：{} 个源，{} 条内容，耗时 {} ms", linkList.size(), list.size(), System.currentTimeMillis() - start);
        return list;
    }

    /**
     * 处理单个RSS源，带有超时控制
     *
     * @param link    包含RSS地址的链接对象
     * @param rssList 用于收集结果的列表
     */
    private void processFeedWithTimeout(Link link, List<Rss> rssList) {
        try {
            UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", link.getRss());
            HttpURLConnection connection = (HttpURLConnection) new URL(link.getRss()).openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(false);
            // 部分站点会拒绝默认的 Java UA，带上常规 UA 以提高抓取成功率
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; ThriveX-RSS/1.0)");
            connection.setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml, */*");

            try (InputStream input = connection.getInputStream()) {
                SyndFeed feed = new SyndFeedInput().build(new XmlReader(input));

                // 使用Stream处理并限制数量
                List<Rss> limitedItems = feed.getEntries().stream()
                        .sorted(Comparator.comparing(SyndEntry::getPublishedDate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
                        .limit(5)
                        .map(data -> {
                            Rss rss = new Rss();
                            rss.setImage(link.getImage());
                            rss.setEmail(link.getEmail());
                            rss.setType(link.getTypeId() != null ? typeCache.get(link.getTypeId()) : null);
                            rss.setAuthor(data.getAuthor() != null ? data.getAuthor() : "");
                            rss.setTitle(data.getTitle() != null ? data.getTitle() : "");
                            rss.setDescription(data.getDescription() != null ? data.getDescription().getValue() : "");
                            rss.setUrl(data.getLink());
                            // 部分源的条目缺少发布时间，依次回退到条目更新时间、源发布时间
                            Date date = data.getPublishedDate() != null ? data.getPublishedDate() : data.getUpdatedDate();
                            if (date == null) date = feed.getPublishedDate();
                            rss.setCreateTime(date != null ? date.getTime() : 0L);
                            return rss;
                        })
                        .collect(Collectors.toList());

                rssList.addAll(limitedItems);
            }
        } catch (CustomException | ConnectException e) {
            log.warn("RSS 已拦截或不可达: {}", link.getRss());
        } catch (Exception e) {
            log.warn("RSS 解析失败: {} - {}", link.getRss(), e.getMessage());
        }
    }

    /**
     * Bean销毁前的清理方法
     * 关闭线程池，释放资源
     */
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
