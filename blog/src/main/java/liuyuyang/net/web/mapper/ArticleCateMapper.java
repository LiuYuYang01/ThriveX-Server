package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.ArticleCate;
import liuyuyang.net.vo.cate.CateArticleCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCateMapper extends BaseMapper<ArticleCate> {
    @Select("SELECT cate_id AS cid, COUNT(*) AS count FROM article_cate GROUP BY cate_id")
    List<CateArticleCountVO> getCateArticleCountByCateId();
}
