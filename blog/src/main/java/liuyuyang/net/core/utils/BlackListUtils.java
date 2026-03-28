package liuyuyang.net.core.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 黑名单工具类
 */
@Component
public class BlackListUtils {

    // 黑名单持续时间（分钟）
    @Value("${blog.limit.blacklist.duration:30}")
    private int blacklistDuration;

    // 黑名单缓存
    private final Cache<String, Long> blacklist = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(10000)
            .build();

    /**
     * 将IP加入黑名单
     *
     * @param ip 要加入黑名单的IP地址
     */
    public void addToBlacklist(String ip) {
        blacklist.put(ip, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(blacklistDuration));
    }


    /**
     * 检查IP是否在黑名单中
     *
     * @param ip 要检查的IP地址
     * @return 如果在黑名单中返回true，否则返回false
     */
    public boolean isBlacklisted(String ip) {
        Long expireTime = blacklist.getIfPresent(ip);
        if (expireTime == null) {
            return false;
        }

        // 检查是否已过期
        if (System.currentTimeMillis() > expireTime) {
            // 已过期，从黑名单中移除
            blacklist.invalidate(ip);
            return false;
        }

        return true;
    }
}