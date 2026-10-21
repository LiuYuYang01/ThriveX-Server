package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Article;
import liuyuyang.net.vo.analysis.SiteSummaryVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章复杂查询见 resources/mapper/ArticleMapper.xml
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    // 站点内容概览：可见文章的累计篇数/字数/浏览/获赞
    SiteSummaryVO selectSiteSummary();
}
