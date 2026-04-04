package liuyuyang.net.vo.cate;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.Cate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CateVO extends Cate {
    @TableField(exist = false)
    @ApiModelProperty(value = "该分类下文章数量", example = "10")
    private Integer count;

    @TableField(exist = false)
    private List<CateVO> children = new ArrayList<>();
}
