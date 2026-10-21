package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 标签复杂查询见 resources/mapper/TagMapper.xml
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    // 每个标签关联的可见文章数
    List<Tag> staticArticleCount();
}
