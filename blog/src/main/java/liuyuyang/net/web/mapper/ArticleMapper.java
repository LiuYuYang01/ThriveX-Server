package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.model.Article;
import liuyuyang.net.vo.analysis.SiteSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章复杂查询见 resources/mapper/ArticleMapper.xml
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    // 站点内容概览：可见文章的累计篇数/字数/浏览/获赞
    SiteSummaryVO selectSiteSummary();

    // 分页查询文章（排除 content 大字段，列表页用）
    Page<Article> selectArticlePage(Page<Article> page, @Param("ids") List<Integer> ids);

    // 按 id 批量查询文章（排除 content 大字段）
    List<Article> selectBatchWithoutContent(@Param("ids") List<Integer> ids);
}
