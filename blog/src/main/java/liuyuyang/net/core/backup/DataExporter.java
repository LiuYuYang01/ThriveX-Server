package liuyuyang.net.core.backup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * 备份导出格式策略：目前仅有 JSON 实现，SQL 格式等后续实现注册为 Spring Bean 后，
 * 服务层按 {@link #format()} 自动发现并分发（见 BackupService 的导出器装配）。
 * <p>
 * 实现约定：方法内部必须在同一数据库事务（一致性快照）内完成全部表的读取，
 * 采用流式/分批写出以保证内存有界，并在写出过程中计算文件 SHA-256 随结果返回。
 */
public interface DataExporter {

    /** 导出格式标识，与 backup_record.format 字段对应 */
    String format();

    /** 将全量数据导出到目标文件，返回统计信息（各表行数、总行数、文件校验值） */
    ExportResult export(Path target) throws IOException;

    /**
     * @param checksum   导出文件整体 SHA-256（十六进制小写）
     * @param tableStats 各表导出行数，key 为表名
     * @param rowTotal   全部表行数合计
     */
    record ExportResult(String checksum, Map<String, Long> tableStats, long rowTotal) {
        public int tableCount() {
            return tableStats.size();
        }
    }
}
