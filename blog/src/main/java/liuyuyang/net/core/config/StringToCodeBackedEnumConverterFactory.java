package liuyuyang.net.core.config;

import liuyuyang.net.enums.CodeBackedEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StringToCodeBackedEnumConverterFactory implements ConverterFactory<String, CodeBackedEnum> {
    @Override
    public <T extends CodeBackedEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            if (source == null || source.isEmpty()) {
                return null;
            }
            String s = source.trim();
            T[] constants = targetType.getEnumConstants();
            if (constants == null) {
                throw new IllegalStateException("不是枚举类型: " + targetType.getName());
            }
            for (T constant : constants) {
                if (constant.getCode().equalsIgnoreCase(s)) {
                    return constant;
                }
            }
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Enum<?>> raw = (Class<? extends Enum<?>>) (Class<?>) targetType;
                @SuppressWarnings({ "unchecked", "rawtypes" })
                T byName = (T) Enum.valueOf((Class) raw, s.toUpperCase(Locale.ROOT));
                return byName;
            } catch (IllegalArgumentException ignored) {
                // fall through
            }
            throw new IllegalArgumentException("不支持的枚举值: " + source);
        };
    }
}
