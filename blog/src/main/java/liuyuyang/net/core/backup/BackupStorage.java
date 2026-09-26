package liuyuyang.net.core.backup;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * 备份文件存储抽象：按"私有文件 + key 存取"语义设计，实现禁止返回公开访问 URL，
 * 与 {@code core.storage} 面向公开图片的存储抽象是两套契约，不可混用。
 * <p>
 * 当前实现：{@link LocalBackupStorage}（本盘）。后续接入第三方对象存储（七牛私有空间、
 * S3 兼容等）时新增实现类即可，{@code backup_record.storage} 记录实现返回的 {@link #type()}，
 * 下载/删除按记录中的 storage 分发，前端无需改动。
 */
public interface BackupStorage {

    /** 存储类型标识，与 backup_record.storage 字段对应 */
    String type();

    /**
     * 创建一个临时文件供导出器写入，返回其路径。
     * 本地实现放在备份目录内（保证后续落位可原子重命名），调用方负责在失败时清理。
     */
    Path newTempFile(String fileName) throws IOException;

    /** 将导出完成的临时文件落位（本盘实现为原子重命名，远程实现为上传），返回存储 key */
    String store(Path tempFile, String fileName) throws IOException;

    /** 打开备份文件的读取流，调用方负责关闭 */
    InputStream load(String key) throws IOException;

    /** 删除备份文件，文件不存在时返回 false 而不抛错 */
    boolean delete(String key);
}
