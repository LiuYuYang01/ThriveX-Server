package liuyuyang.net.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    // 允许的请求次数
    long tokens() default 0;

    // 请求周期
    long duration() default 0;

    // 错误消息
    String message() default "操作过于频繁，请稍后再试";
}
