package liuyuyang.net.enums.comment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import liuyuyang.net.enums.CodeBackedEnum;

import java.util.Objects;

@Getter
public enum CommentPatternEnum implements CodeBackedEnum {
    TREE("tree"),
    LIST("list");

    private final String code;

    CommentPatternEnum(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static CommentPatternEnum fromJson(String code) {
        if (code == null) {
            return null;
        }

        for (CommentPatternEnum s : values()) {
            if (Objects.equals(s.code, code)) {
                return s;
            }
        }

        throw new IllegalArgumentException("不支持的评论展示模式: " + code);
    }
}
