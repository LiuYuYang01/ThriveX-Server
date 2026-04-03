package liuyuyang.net.enums.cate;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum CateTypeEnum {
    CATE("cate", "分类"),
    NAV("nav", "导航");

    @EnumValue
    private final String value;

    @Getter
    private final String desc;

    CateTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CateTypeEnum fromJson(String value) {
        if (value == null) {
            return null;
        }
        for (CateTypeEnum s : values()) {
            if (s.value.equals(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知分类类型: " + value);
    }
}
