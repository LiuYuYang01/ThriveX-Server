package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.SearchLog;
import liuyuyang.net.vo.analysis.HotKeywordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchLogMapper extends BaseMapper<SearchLog> {
    // 统计周期内搜索次数最多的关键词
    @Select("select keyword, count(*) as count "
            + "from search_log "
            + "where create_time >= #{startTime} "
            + "group by keyword "
            + "order by count desc "
            + "limit #{limit}")
    List<HotKeywordVO> selectHotKeywords(@Param("startTime") Long startTime, @Param("limit") Integer limit);
}
