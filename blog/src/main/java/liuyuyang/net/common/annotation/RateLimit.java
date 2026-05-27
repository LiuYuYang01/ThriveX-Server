package liuyuyang.net.common.annotation;

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
    /** 每周期允许次数，0 表示使用全局配置 blog.limit.tokens */
    long tokens() default 0;

    /** 周期秒数，0 表示使用全局配置 blog.limit.duration */
    long duration() default 0;

    /** 限流提示文案，为空则使用默认文案 */
    String message() default "";
}