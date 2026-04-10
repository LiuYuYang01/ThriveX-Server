package liuyuyang.net.enums.wall;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum WallAuditStatusEnum {
    PENDING(0, "待审核"),
    APPROVED(1, "审核通过");

    // @EnumValue：告诉 MyBatis-Plus，当这个枚举与数据库交互时，使用 value 字段作为存储值
    @EnumValue
    private final int value;

    @Getter
    private final String desc;

    WallAuditStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static WallAuditStatusEnum fromJson(Integer value) {
        if (value == null) {
            return null;
        }
        for (WallAuditStatusEnum s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知留言审核状态: " + value);
    }
}
