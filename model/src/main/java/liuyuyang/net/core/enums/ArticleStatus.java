package liuyuyang.net.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum ArticleStatus {
    DEFAULT(1, "正常"),
    NO_HOME(2, "首页隐藏"),
    HIDE(3, "全站隐藏");

    @EnumValue
    private final int value;

    @Getter
    private final String desc;

    ArticleStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static ArticleStatus fromJson(Integer value) {
        if (value == null) return null;

        for (ArticleStatus s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        
        throw new IllegalArgumentException("未知文章状态: " + value);
    }
}
