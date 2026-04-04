package liuyuyang.net.enums;

/**
 * 标记「对外用字符串 code 表示」的枚举（与 {@code @JsonValue} 的 getCode 一致）。
 * <p>
 * Spring MVC 查询参数绑定由 {@code StringToCodeBackedEnumConverterFactory} 统一处理，
 * 无需再为每种枚举单独写 {@code Converter} Bean。
 */
public interface CodeBackedEnum {
    String getCode();
}
