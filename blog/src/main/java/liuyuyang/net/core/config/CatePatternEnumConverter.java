package liuyuyang.net.core.config;

import liuyuyang.net.enums.cate.CatePatternEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Spring MVC 对查询参数、表单等使用 {@link org.springframework.core.convert.ConversionService} 绑定，
 * 默认按 {@link Enum#valueOf}（整段字符串须等于常量名）。本类将字符串转为枚举，与 Jackson 的
 * {@code @JsonCreator} / {@code fromJson} 行为一致（tree、list），并兼容 TREE、LIST。
 *
 * <p>这是 Spring Framework 文档中推荐的自定义绑定方式：实现 {@link Converter} 并注册为 Bean。
 */
@Component
public class CatePatternEnumConverter implements Converter<String, CatePatternEnum> {

    @Override
    public CatePatternEnum convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        String s = source.trim();
        try {
            return CatePatternEnum.fromJson(s);
        } catch (IllegalArgumentException ignored) {
            return CatePatternEnum.valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}
