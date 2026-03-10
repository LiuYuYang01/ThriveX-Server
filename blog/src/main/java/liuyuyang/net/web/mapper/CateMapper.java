package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CateMapper extends BaseMapper<Cate> {

    @Select("SELECT MIN(c.name) AS name, COUNT(*) AS count FROM article a JOIN article_cate ac ON a.id = ac.article_id JOIN cate c ON c.id = ac.cate_id GROUP BY c.id")
    List<CateArticleCount> cateArticleCount();
}
