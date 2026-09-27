package liuyuyang.net.core.backup;

import liuyuyang.net.web.mapper.BackupDataMapper;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 备份范围的统一策略：各格式导出器共用同一份表清单与安全校验，保证不同格式的备份覆盖一致。
 */
public final class BackupTables {

    // 日志表体积大且丢失无感，默认不备份；backup_record 是备份体系自身，不参与自嵌套
    public static final Set<String> EXCLUDED = Set.of(
            "operation_log", "search_log", "article_view_log", "backup_record");

    // 表名最终会以 ${} 方式拼进 SQL，只放行 information_schema 返回的规范标识符
    public static final Pattern SAFE_NAME = Pattern.compile("^[a-z0-9_]+$");

    private BackupTables() {
    }

    /** 当前库中可备份的基础表清单（排除视图与排除表，按名称排序） */
    public static List<String> exportable(BackupDataMapper mapper) {
        return mapper.listTableNames().stream()
                .filter(name -> name != null && SAFE_NAME.matcher(name).matches())
                .filter(name -> !EXCLUDED.contains(name))
                .sorted()
                .toList();
    }
}
