package liuyuyang.net.dto.assistant;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AssistantFormDTO {
    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(value = "助手名称", example = "DeepSeek", required = true)
    private String name;
    @TableField("`key`")
    @ApiModelProperty(value = "API 密钥", example = "xxxxxxxxxxxxxxxxxxxxxxxxxx")
    private String key;
    @ApiModelProperty(value = "API 地址", example = "https://api.deepseek.com")
    private String url;
    @ApiModelProperty(value = "API 模型", example = "deepseek-chat")
    private String model;
}
