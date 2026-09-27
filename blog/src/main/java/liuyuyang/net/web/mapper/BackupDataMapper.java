package liuyuyang.net.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 备份导出专用通用查询：面向表名字符串而非具体实体，schema 演进无需改动本类。
 * 表名以 ${} 拼接（无法参数化），调用方必须先经 JsonDataExporter 的 SAFE_TABLE_NAME 白名单校验。
 */
@Mapper
public interface BackupDataMapper {

    @Select("SELECT table_name FROM information_schema.tables "
            + "WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE' ORDER BY table_name")
    List<String> listTableNames();

    @Select("SELECT DATABASE()")
    String currentDatabase();

    @Select("SHOW CREATE TABLE `${tableName}`")
    Map<String, Object> showCreateTable(@Param("tableName") String tableName);

    @Select("SELECT COUNT(*) FROM `${tableName}`")
    long countTable(@Param("tableName") String tableName);

    @Select("SELECT * FROM `${tableName}` LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectBatch(@Param("tableName") String tableName,
                                          @Param("offset") long offset,
                                          @Param("limit") int limit);
}
