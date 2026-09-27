package liuyuyang.net.core.backup;

import liuyuyang.net.web.mapper.BackupDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL 格式全量导出（应用内生成，不依赖 mysqldump，远程数据库/Docker/面板环境零部署成本）：
 * 每表输出 DROP TABLE IF EXISTS + SHOW CREATE TABLE 的完整结构，再逐行输出带列名的 INSERT。
 * <p>
 * 生成的 .sql 可被 mysql 客户端 / Navicat / 宝塔"数据库导入"直接灌库，是应用故障场景下的
 * 万能兜底；字符串转义与 mysqldump 同规则（反斜杠、引号、控制符），导入前 SET NAMES utf8mb4。
 * 一致性与内存约束：单一只读事务快照 + 分批流式写出。
 */
@Slf4j
@Component
@Transactional(readOnly = true)
public class SqlDataExporter {
    private static final int VERSION = 1;
    private static final int BATCH_SIZE = 500;

    private final BackupDataMapper backupDataMapper;

    public SqlDataExporter(BackupDataMapper backupDataMapper) {
        this.backupDataMapper = backupDataMapper;
    }

    public ExportResult export(Path target) throws IOException {
        List<String> tables = BackupTables.exportable(backupDataMapper);
        if (tables.isEmpty()) {
            throw new IOException("未发现任何可备份的数据表");
        }

        MessageDigest digest = sha256();
        Map<String, Long> stats = new LinkedHashMap<>();
        long rowTotal = 0;
        try (OutputStream out = Files.newOutputStream(target);
             DigestOutputStream digestOut = new DigestOutputStream(out, digest)) {

            writeHeader(digestOut, tables);
            for (String table : tables) {
                digestOut.write(ddlOf(table).getBytes(StandardCharsets.UTF_8));
                long rows = 0;
                long offset = 0;
                List<Map<String, Object>> batch;
                while (!(batch = backupDataMapper.selectBatch(table, offset, BATCH_SIZE)).isEmpty()) {
                    for (Map<String, Object> row : batch) {
                        digestOut.write(insertOf(table, row).getBytes(StandardCharsets.UTF_8));
                        rows++;
                    }
                    offset += batch.size();
                    if (batch.size() < BATCH_SIZE) break;
                }
                stats.put(table, rows);
                rowTotal += rows;
            }
            digestOut.write("SET FOREIGN_KEY_CHECKS = 1;\n".getBytes(StandardCharsets.UTF_8));
        }

        String checksum = HexFormat.of().formatHex(digest.digest());
        log.info("SQL 备份导出完成：{} 张表 / {} 行 / {} 字节", tables.size(), rowTotal, Files.size(target));
        return new ExportResult(checksum, stats, rowTotal);
    }

    /** 文件头：元信息注释 + 导入会话设置，注释中带 header 元数据供人工或工具识别 */
    private void writeHeader(OutputStream out, List<String> tables) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("-- ThriveX Database Backup\n");
        sb.append("-- Format Version: ").append(VERSION).append('\n');
        sb.append("-- Database: ").append(backupDataMapper.currentDatabase()).append('\n');
        sb.append("-- Created At: ").append(LocalDateTime.now().withNano(0)).append('\n');
        sb.append("-- Tables: ").append(tables.size()).append('\n');
        sb.append("--\n");
        sb.append("SET NAMES utf8mb4;\n");
        sb.append("SET FOREIGN_KEY_CHECKS = 0;\n");
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** SHOW CREATE TABLE 的结果里取以 CREATE 开头的那个值，即建表语句 */
    private String ddlOf(String table) {
        Map<String, Object> row = backupDataMapper.showCreateTable(table);
        if (row == null || row.isEmpty()) {
            throw new IllegalStateException("SHOW CREATE TABLE 未返回结构：" + table);
        }
        return row.values().stream()
                .filter(v -> v != null && v.toString().trim().toUpperCase().startsWith("CREATE"))
                .findFirst()
                .map(v -> v + ";\n\n")
                .orElseThrow(() -> new IllegalStateException("SHOW CREATE TABLE 返回异常：" + table));
    }

    private String insertOf(String table, Map<String, Object> row) {
        StringBuilder sb = new StringBuilder(256).append("INSERT INTO `").append(table).append("` (");
        StringBuilder values = new StringBuilder(" VALUES (");
        int i = 0;
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (i++ > 0) {
                sb.append(", ");
                values.append(", ");
            }
            sb.append('`').append(entry.getKey()).append('`');
            values.append(sqlValue(entry.getValue()));
        }
        return sb.append(")").append(values).append(");\n").toString();
    }

    /** 按值的实际 JDBC 类型决定加不加引号，避免数字/时间被当成字符串破坏导入 */
    private String sqlValue(Object v) {
        if (v == null) return "NULL";
        if (v instanceof Boolean b) return b ? "1" : "0";
        if (v instanceof Byte || v instanceof Short || v instanceof Integer
                || v instanceof Long || v instanceof java.math.BigInteger
                || v instanceof Float || v instanceof Double
                || v instanceof java.math.BigDecimal) {
            return String.valueOf(v);
        }
        if (v instanceof byte[] bytes) return "0x" + HexFormat.of().formatHex(bytes);
        if (v instanceof LocalDateTime t) return quote(t.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (v instanceof LocalDate d) return quote(d.format(DateTimeFormatter.ISO_LOCAL_DATE));
        if (v instanceof LocalTime t) return quote(t.format(DateTimeFormatter.ISO_LOCAL_TIME));
        return quote(String.valueOf(v));
    }

    private String quote(String s) {
        return "'" + escape(s) + "'";
    }

    /** 与 mysqldump 同规则的转义：\0 \n \r \Z ' " \（依赖默认 sql_mode，未开启 NO_BACKSLASH_ESCAPES） */
    private String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\0' -> sb.append("\\0");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\u001a' -> sb.append("\\Z");
                case '\\' -> sb.append("\\\\");
                case '\'' -> sb.append("\\'");
                case '"' -> sb.append("\\\"");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM 不支持 SHA-256", e);
        }
    }

    /**
     * @param checksum   导出文件整体 SHA-256（十六进制小写）
     * @param tableStats 各表导出行数，key 为表名
     * @param rowTotal   全部表行数合计
     */
    public record ExportResult(String checksum, Map<String, Long> tableStats, long rowTotal) {
        public int tableCount() {
            return tableStats.size();
        }
    }
}
