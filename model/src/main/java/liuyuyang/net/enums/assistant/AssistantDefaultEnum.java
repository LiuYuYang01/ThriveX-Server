package liuyuyang.net.enums.assistant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum AssistantDefaultEnum {
    NOT_DEFAULT(0, "未选中"),
    DEFAULT(1, "默认");

    @EnumValue
    private final int value;

    @Getter
    private final String desc;

    AssistantDefaultEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static AssistantDefaultEnum fromJson(Integer value) {
        if (value == null) {
            return null;
        }
        for (AssistantDefaultEnum item : values()) {
            if (item.value == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知助手默认状态: " + value);
    }
}
