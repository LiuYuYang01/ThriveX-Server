package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("wall_cate")
public class WallCate {
    @TableId(type = IdType.AUTO)
    @Schema(description = "标签ID")
    private Integer id;

    @Schema(description = "分类名称", example = "全部", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "分类标识", example = "all", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mark;

    @TableField("`order`")
    @Schema(description = "分类顺序", example = "1")
    private Integer order;
}
