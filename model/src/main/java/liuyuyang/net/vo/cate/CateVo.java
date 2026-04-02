package liuyuyang.net.vo.cate;

import com.baomidou.mybatisplus.annotation.TableField;
import liuyuyang.net.model.Cate;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CateVo extends Cate {
    @TableField(exist = false)
    private List<CateVo> children = new ArrayList<>();
}
