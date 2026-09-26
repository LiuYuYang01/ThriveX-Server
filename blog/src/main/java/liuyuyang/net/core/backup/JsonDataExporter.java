package liuyuyang.net.core.backup;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.web.mapper.BackupDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * JSON 格式全量导出：文件结构为 { header: 元信息, data: { 表名: [行...] } }，
 * 列名保持数据库原始 snake_case，时间由 Jackson 按配置序列化，供将来"恢复"功能直接校验回灌。
 * <p>
 * 一致性：类级 readOnly 事务保证所有表读取走同一连接、拿到 InnoDB 一致性快照，
 * 导出期间前台的写入不会造成表间数据撕裂。
 * 内存安全：逐表分批（BATCH_SIZE 行）查询、边写边丢，文章正文等长文本不会整体进堆；
 * 同时经 DigestOutputStream 计算整文件 SHA-256，无需二次读盘。
 */
@Slf4j
@Component
@Transactional(readOnly = true)
public class JsonDataExporter implements DataExporter {
    public static final String FORMAT = "json";
    private static final int VERSION = 1;
    private static final int BATCH_SIZE = 500;

    // 日志表体积大且丢失无感，默认不备份；backup_record 是备份体系自身，不参与自嵌套
    private static final Set<String> EXCLUDED_TABLES = Set.of(
            "operation_log", "search_log", "article_view_log", "backup_record");

    // 表名最终会以 ${} 方式拼进 SQL，只放行 information_schema 返回的规范标识符
    private static final Pattern SAFE_TABLE_NAME = Pattern.compile("^[a-z0-9_]+$");

    private final BackupDataMapper backupDataMapper;
    private final ObjectMapper objectMapper;

    public JsonDataExporter(BackupDataMapper backupDataMapper, ObjectMapper objectMapper) {
        this.backupDataMapper = backupDataMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public String format() {
        return FORMAT;
    }

    @Override
    public ExportResult export(Path target) throws IOException {
        List<String> tables = listExportableTables();
        if (tables.isEmpty()) {
            throw new IOException("未发现任何可备份的数据表");
        }

        MessageDigest digest = sha256();
        Map<String, Long> stats = new LinkedHashMap<>();
        long rowTotal = 0;
        try (OutputStream out = Files.newOutputStream(target);
             DigestOutputStream digestOut = new DigestOutputStream(out, digest);
             JsonGenerator gen = objectMapper.getFactory().createGenerator(digestOut, JsonEncoding.UTF8)) {

            gen.writeStartObject();

            // header：先 COUNT 一遍拿到元信息；与后面 data 的读取同处一个事务快照，行数一致
            gen.writeObjectFieldStart("header");
            gen.writeStringField("app", "ThriveX");
            gen.writeNumberField("version", VERSION);
            gen.writeStringField("format", FORMAT);
            gen.writeStringField("database", backupDataMapper.currentDatabase());
            gen.writeStringField("createdAt", LocalDateTime.now().withNano(0).toString());
            gen.writeNumberField("tableCount", tables.size());
            gen.writeNumberField("rowTotal", countAll(tables, stats));
            gen.writeObjectFieldStart("tables");
            for (Map.Entry<String, Long> entry : stats.entrySet()) {
                gen.writeNumberField(entry.getKey(), entry.getValue());
            }
            gen.writeEndObject();
            gen.writeEndObject();

            // data：逐表分批流式写出，以实际写出行数修正统计
            gen.writeObjectFieldStart("data");
            for (String table : tables) {
                gen.writeArrayFieldStart(table);
                long rows = 0;
                long offset = 0;
                List<Map<String, Object>> batch;
                while (!(batch = backupDataMapper.selectBatch(table, offset, BATCH_SIZE)).isEmpty()) {
                    for (Map<String, Object> row : batch) {
                        gen.writeObject(row);
                        rows++;
                    }
                    offset += batch.size();
                    if (batch.size() < BATCH_SIZE) break;
                }
                gen.writeEndArray();
                stats.put(table, rows);
                rowTotal += rows;
            }
            gen.writeEndObject();

            gen.writeEndObject();
            gen.flush();
        }

        // 流关闭后所有字节均已过 DigestOutputStream，此时摘要才完整
        String checksum = HexFormat.of().formatHex(digest.digest());
        log.info("数据库备份导出完成：{} 张表 / {} 行 / {} 字节", tables.size(), rowTotal, Files.size(target));
        return new ExportResult(checksum, stats, rowTotal);
    }

    private List<String> listExportableTables() {
        return backupDataMapper.listTableNames().stream()
                .filter(name -> name != null && SAFE_TABLE_NAME.matcher(name).matches())
                .filter(name -> !EXCLUDED_TABLES.contains(name))
                .sorted()
                .toList();
    }

    private long countAll(List<String> tables, Map<String, Long> stats) {
        long total = 0;
        for (String table : tables) {
            long count = backupDataMapper.countTable(table);
            stats.put(table, count);
            total += count;
        }
        return total;
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM 不支持 SHA-256", e);
        }
    }
}
