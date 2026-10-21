package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.SearchLog;
import liuyuyang.net.vo.analysis.HotKeywordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索日志复杂查询见 resources/mapper/SearchLogMapper.xml
 */
@Mapper
public interface SearchLogMapper extends BaseMapper<SearchLog> {

    // 统计周期内搜索次数最多的关键词
    List<HotKeywordVO> selectHotKeywords(@Param("startTime") Long startTime, @Param("limit") Integer limit);
}
