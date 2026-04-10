package liuyuyang.net.vo.link;

import com.baomidou.mybatisplus.annotation.TableField;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.LinkType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LinkVO extends Link {
    @TableField(exist = false)
    private LinkType type;
}
