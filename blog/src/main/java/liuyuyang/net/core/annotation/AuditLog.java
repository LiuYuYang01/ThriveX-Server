package liuyuyang.net.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 标注在需要强制记录操作日志的接口上（如免 Token 的登录接口），
 * 未标注的管理端写接口（POST/PUT/PATCH/DELETE）会被切面自动记录
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    // 操作模块，如：文章管理
    String module() default "";

    // 操作类型，如：新增/修改/删除/登录
    String type() default "";

    // 操作描述
    String description() default "";
}
