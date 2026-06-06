package liuyuyang.net.core.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.BlackListUtils;
import liuyuyang.net.core.utils.IpUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private BlackListUtils blackListUtils;

    @Value("${blog.limit.tokens:20}")
    private long defaultTokens;

    @Value("${blog.limit.duration:60}")
    private long defaultDuration;

    @Value("${blog.limit.blacklist.threshold:5}")
    private int blacklistThreshold;

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(5000)
            .build();

    private final Cache<String, Integer> rateLimitCounts = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(10000)
            .build();

    @Around("@annotation(liuyuyang.net.core.annotation.RateLimit)")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = IpUtils.getRealIp(request);
        if (blackListUtils.isBlacklisted(ip)) {
            throw new CustomException("您已被暂时限制访问，请稍后再试");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimit rateLimit = signature.getMethod().getAnnotation(RateLimit.class);
        long tokens = rateLimit.tokens() > 0 ? rateLimit.tokens() : defaultTokens;
        long duration = rateLimit.duration() > 0 ? rateLimit.duration() : defaultDuration;

        String key = joinPoint.getSignature().toShortString() + ":" + ip + ":" + tokens + ":" + duration;
        Bucket bucket = buckets.get(key, k -> createNewBucket(tokens, duration));

        if (bucket != null && bucket.tryConsume(1)) {
            rateLimitCounts.invalidate(ip);
            return joinPoint.proceed();
        }

        int count = handleRateLimitExceeded(ip);
        throw new CustomException(buildRateLimitMessage(rateLimit, duration, count));
    }

    private String buildRateLimitMessage(RateLimit rateLimit, long duration, int exceedCount) {
        if (!rateLimit.message().isEmpty()) {
            return rateLimit.message();
        }
        if (exceedCount >= blacklistThreshold) {
            return "操作过于频繁，已暂时限制访问，请稍后再试";
        }
        return String.format("操作过于频繁，请 %d 秒后再试，距离触发限制还剩 %d 次",
                duration, blacklistThreshold - exceedCount);
    }

    private Bucket createNewBucket(long tokens, long duration) {
        Refill refill = Refill.intervally(tokens, Duration.ofSeconds(duration));
        Bandwidth limit = Bandwidth.classic(tokens, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private int handleRateLimitExceeded(String ip) {
        Integer count = rateLimitCounts.getIfPresent(ip);
        count = count == null ? 1 : count + 1;
        rateLimitCounts.put(ip, count);

        if (count >= blacklistThreshold) {
            blackListUtils.addToBlacklist(ip);
        }
        return count;
    }
}
