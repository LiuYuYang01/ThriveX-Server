package liuyuyang.net.enums.link;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum LinkStatusEnum {
    PENDING(0, "待审核"),
    APPROVED(1, "审核通过");

    @EnumValue
    private final int value;

    @Getter
    private final String desc;

    LinkStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static LinkStatusEnum fromJson(Integer value) {
        if (value == null) {
            return null;
        }
        for (LinkStatusEnum s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知友联审核状态: " + value);
    }
}
