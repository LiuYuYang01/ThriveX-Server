package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class Config {
    @TableId(type = IdType.AUTO)
    @Schema(description = "环境配置ID")
    private Integer id;

    @Schema(description = "配置名称", example = "database_config", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "配置值(JSON格式)", example = "{\"name\":\"宇阳\"}", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> value;

    @Schema(description = "配置备注")
    private String notes;
}
