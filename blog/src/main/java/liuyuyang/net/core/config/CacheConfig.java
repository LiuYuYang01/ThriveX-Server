package liuyuyang.net.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 缓存配置：基于 Caffeine 的进程内缓存
 *
 * 注意：此前项目未开启 @EnableCaching，导致 @Cacheable 注解完全不生效（如 RSS 聚合接口每次请求都在实时抓取外部源）。
 *
 * 命名缓存约定（各业务通过 @Cacheable/@CacheEvict 的 cacheNames 引用）：
 *  - siteSummary：站点内容概览，60s 短 TTL（浏览量高频变动，用短过期兜底而非精确失效）
 *  - tagList：标签列表（含文章数），60s 短 TTL
 *  - rssCache：RSS 聚合结果，由 RssServiceImpl 定时写回，不走 TTL 失效
 * 未显式注册的缓存名走默认 Caffeine 配置（2 小时过期、最大 64 条）。
 */
@Configuration
@EnableCaching
public class CacheConfig {
    /** 站点概览缓存名 */
    public static final String CACHE_SITE_SUMMARY = "siteSummary";
    /** 标签列表缓存名 */
    public static final String CACHE_TAG_LIST = "tagList";
    /** 网站配置缓存名（按 name 缓存） */
    public static final String CACHE_WEB_CONFIG = "webConfig";
    /** 环境配置缓存名（按 name 缓存） */
    public static final String CACHE_ENV_CONFIG = "envConfig";
    /** 热门文章缓存名（按 count 缓存） */
    public static final String CACHE_HOT_ARTICLE = "hotArticle";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 默认缓存：2 小时过期兜底；正常情况下由各业务的定时任务主动刷新
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(2))
                .maximumSize(64));

        // 站点概览：高频读、依赖浏览量等高频变动字段，短 TTL 容忍短暂不一致
        cacheManager.registerCustomCache(CACHE_SITE_SUMMARY, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(60))
                .maximumSize(4)
                .build());

        // 标签列表：数据量小、高频读，短 TTL 容忍标签文章数的短暂不一致
        cacheManager.registerCustomCache(CACHE_TAG_LIST, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(60))
                .maximumSize(4)
                .build());

        // 网站配置：管理员低频改、前台高频读（首页/评论/留言/SEO 每次访问都查），5min TTL + 写方法精确失效
        cacheManager.registerCustomCache(CACHE_WEB_CONFIG, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .maximumSize(32)
                .build());

        // 环境配置：同上，邮件/存储/统计/验证码逐次调用
        cacheManager.registerCustomCache(CACHE_ENV_CONFIG, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .maximumSize(32)
                .build());

        // 热门文章：浏览量高频变动导致难以精确失效，用 60s 短 TTL；文章增删改时主动失效
        cacheManager.registerCustomCache(CACHE_HOT_ARTICLE, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(60))
                .maximumSize(8)
                .build());

        return cacheManager;
    }
}
