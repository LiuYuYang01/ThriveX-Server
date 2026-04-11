package liuyuyang.net.core.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 将查询参数中的字符串转为枚举：优先调用各枚举上的 {@code fromJson(Integer)} / {@code fromJson(String)}（与 JSON 一致），否则回退为 {@link Enum#valueOf(Class, String)}。
 * <p>
 * 需在 {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addFormatters} 中注册，
 * 以便在默认 {@code StringToEnumConverterFactory} 之前生效（同类型转换器后注册优先）。
 */
@SuppressWarnings("rawtypes")
public final class JsonBackedEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    private static final class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {

        private final Class<T> enumType;
        private final Method fromJsonInteger;
        private final Method fromJsonString;

        StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
            this.fromJsonInteger = findMethod(enumType, "fromJson", Integer.class);
            this.fromJsonString = findMethod(enumType, "fromJson", String.class);
        }

        private static Method findMethod(Class<?> type, String name, Class<?> param) {
            try {
                return type.getMethod(name, param);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }

        @Override
        public T convert(String source) {
            if (source == null) {
                return null;
            }
            String s = source.trim();
            if (s.isEmpty()) {
                return null;
            }
            try {
                if (fromJsonInteger != null && s.matches("-?\\d+")) {
                    @SuppressWarnings("unchecked")
                    T t = (T) fromJsonInteger.invoke(null, Integer.valueOf(s));
                    return t;
                }
                if (fromJsonString != null) {
                    @SuppressWarnings("unchecked")
                    T t = (T) fromJsonString.invoke(null, s);
                    return t;
                }
                if (fromJsonInteger != null) {
                    @SuppressWarnings("unchecked")
                    T t = (T) fromJsonInteger.invoke(null, Integer.valueOf(s));
                    return t;
                }
            } catch (InvocationTargetException e) {
                Throwable c = e.getCause();
                if (c instanceof RuntimeException) {
                    throw (RuntimeException) c;
                }
                throw new IllegalArgumentException("无法将 \"" + source + "\" 转为 " + enumType.getName(), c);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("无法将 \"" + source + "\" 转为 " + enumType.getName(), e);
            }
            return Enum.valueOf(enumType, s);
        }
    }
}
