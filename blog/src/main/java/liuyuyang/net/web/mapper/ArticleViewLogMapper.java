package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.ArticleViewLog;
import liuyuyang.net.vo.analysis.HotArticleVO;
import liuyuyang.net.vo.analysis.ViewTrendItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 浏览日志复杂查询见 resources/mapper/ArticleViewLogMapper.xml
 */
@Mapper
public interface ArticleViewLogMapper extends BaseMapper<ArticleViewLog> {

    // 统计周期内热门文章（周期浏览量来自浏览日志，总量字段取自文章表）
    List<HotArticleVO> selectHotArticles(@Param("startTime") Long startTime, @Param("limit") Integer limit);

    // 单篇文章按天浏览趋势
    List<ViewTrendItemVO> selectViewTrend(@Param("articleId") Integer articleId, @Param("startTime") Long startTime);
}
