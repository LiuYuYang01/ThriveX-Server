package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    @Select("SELECT t.id, t.name, COALESCE(c.cnt, 0) AS `count` FROM tag t "
            + "LEFT JOIN ("
            + "SELECT at.tag_id AS tag_id, COUNT(DISTINCT a.id) AS cnt "
            + "FROM article_tag at "
            + "INNER JOIN article a ON a.id = at.article_id "
            + "INNER JOIN article_config ac ON ac.article_id = a.id "
            + "AND ac.is_draft = 0 AND ac.is_del = 0 AND COALESCE(ac.status, 1) <> 3 "
            + "GROUP BY at.tag_id"
            + ") c ON c.tag_id = t.id "
            + "ORDER BY `count` DESC")
    List<Tag> staticArticleCount();
}
