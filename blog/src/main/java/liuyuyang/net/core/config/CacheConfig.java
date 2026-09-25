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
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 缓存条目 2 小时过期兜底；正常情况下由各业务的定时任务主动刷新，用户请求不会触发冷启动重建
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(2))
                .maximumSize(64));
        return cacheManager;
    }
}
