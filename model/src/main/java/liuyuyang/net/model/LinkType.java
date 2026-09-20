package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("link_type")
public class LinkType {
    @TableId(type = IdType.AUTO)
    @Schema(description = "网站类型ID")
    private Integer id;
    @Schema(description = "网站类型名称", example = "技术类", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "用户是否可选，0表示不可选 1表示可选", example = "0")
    private Integer isAdmin;
    @TableField("`order`")
    @Schema(description = "网站类型顺序", example = "1")
    private Integer order;
}
