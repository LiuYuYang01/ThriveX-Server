package liuyuyang.net.enums.cate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum CatePatternEnum {
    TREE("tree"),
    LIST("list");

    private final String code;

    CatePatternEnum(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }


    @JsonCreator
    public static CatePatternEnum fromJson(String code) {
        if (code == null) return null;

        for (CatePatternEnum s : values()) {
            if (Objects.equals(s.code, code)) {
                return s;
            }
        }

        throw new IllegalArgumentException("不支持的分类展示模式: " + code);
    }
}
