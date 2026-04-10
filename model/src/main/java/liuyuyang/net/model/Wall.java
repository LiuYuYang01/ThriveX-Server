package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.enums.wall.WallAuditStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wall")
public class Wall extends BaseModel {
    @ApiModelProperty(value = "留言人名称", example = "神秘人", required = true)
    private String name;

    @ApiModelProperty(value = "分类id", example = "1", required = true)
    private Integer cateId;

    @TableField(exist = false)
    @ApiModelProperty(value = "留言分类", example = "全部")
    private WallCate cate;

    @ApiModelProperty(value = "留言墙颜色", example = "#92e6f54d")
    private String color;

    @ApiModelProperty(value = "留言内容", example = "这是一段内容", required = true)
    private String content;

    @ApiModelProperty(value = "邮箱", example = "3311118881@qq.com")
    private String email;

    @ApiModelProperty(value = "审核状态：0 待审核，1 审核通过", example = "1")
    private WallAuditStatusEnum auditStatus;

    @ApiModelProperty(value = "设置与取消精选", example = "1")
    private Integer isChoice;
}
