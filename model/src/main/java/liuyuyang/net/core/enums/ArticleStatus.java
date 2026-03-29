package liuyuyang.net.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum ArticleStatus {
    DEFAULT(1, "正常"),
    NO_HOME(2, "首页隐藏"),
    HIDE(3, "全站隐藏");

    // @EnumValue：告诉 MyBatis-Plus，当这个枚举与数据库交互时，使用 value 字段作为存储值
    // 插入数据时：将枚举的 value（1/2/3）存入数据库
    // 查询数据时：将数据库的整数值（1/2/3）自动映射回对应的枚举
    @EnumValue
    private final int value;

    // @Getter：自动生成 getDesc() 方法，方便获取枚举的中文描述
    @Getter
    private final String desc;

    ArticleStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    // @JsonValue：序列化为 JSON 时得到：1/2/3，而不是 "DEFAULT"/"NO_HOME"/"HIDE"
    @JsonValue
    public int getValue() {
        return value;
    }

    // @JsonCreator：反序列化 JSON 时，根据整数值（1/2/3）找到对应的枚举实例
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
