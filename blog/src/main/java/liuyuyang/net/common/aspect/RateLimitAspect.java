package liuyuyang.net.common.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.BlackListUtils;
import liuyuyang.net.common.utils.IpUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private BlackListUtils blackListUtils;

    // 注入全局配置
    @Value("${blog.limit.tokens:20}")
    private long defaultTokens;

    @Value("${blog.limit.duration:60}")
    private long defaultDuration;

    // 黑名单触发次数阈值
    @Value("${blog.limit.blacklist.threshold:5}")
    private int blacklistThreshold;

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(5000)
            .build();

    // 记录每个IP触发限流的次数
    private final Cache<String, Integer> rateLimitCounts = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(10000)
            .build();

    @Around("@annotation(liuyuyang.net.common.annotation.RateLimit)") // 拦截所有加了 @RateLimit 的方法
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取 Request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        // 2. 获取真实客户端 IP
        String ip = IpUtils.getRealIp(request);

        // 3. 构造唯一的 Key：方法名 + IP (确保不同接口限流互不干扰)
        String methodName = joinPoint.getSignature().toShortString();
        String key = methodName + ":" + ip;

        // 4. 获取或创建桶
        Bucket bucket = buckets.get(key, k -> createNewBucket());

        // 5. 尝试消耗令牌
        if (bucket != null && bucket.tryConsume(1)) {
            // 需要将对应的黑名单数据移除
            rateLimitCounts.invalidate(ip);
            return joinPoint.proceed();
        } else {
            // 触发限流
            handleRateLimitExceeded(ip);
            int count = Optional.ofNullable(rateLimitCounts.getIfPresent(ip))
                    .orElse(0);
            throw new CustomException("操作太快啦，请稍后再试,距触发风控还需要:" + (blacklistThreshold - count));
        }
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.intervally(defaultTokens, Duration.ofSeconds(defaultDuration));
        Bandwidth limit = Bandwidth.classic(defaultTokens, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * 处理限流超限情况
     *
     * @param ip 触发限流的IP地址
     */
    private void handleRateLimitExceeded(String ip) {
        // 增加该IP的限流计数
        Integer count = rateLimitCounts.getIfPresent(ip);
        if (count == null) {
            count = 0;
        }
        count++;

        // 更新限流计数
        rateLimitCounts.put(ip, count);

        // 如果超过阈值，则加入黑名单
        if (count >= blacklistThreshold) {
            blackListUtils.addToBlacklist(ip);
        }
    }
}