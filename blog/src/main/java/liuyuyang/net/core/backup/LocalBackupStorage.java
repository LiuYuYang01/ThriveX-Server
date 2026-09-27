package liuyuyang.net.core.backup;

import liuyuyang.net.core.execption.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;

/**
 * 本盘存储实现：备份文件落在 backup.dir 目录下，key 即文件名（不含任何路径成分）。
 * Docker/宝塔/1Panel 部署时通过 BACKUP_DIR 环境变量或挂载卷重定向目录。
 */
@Component
public class LocalBackupStorage implements BackupStorage {
    private final Path root;

    public LocalBackupStorage(@Value("${backup.dir}") String dir) {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
    }

    @Override
    public String type() {
        return "local";
    }

    @Override
    public Path newTempFile(String fileName) throws IOException {
        Files.createDirectories(root);
        return root.resolve(fileName + ".tmp");
    }

    @Override
    public String store(Path tempFile, String fileName) throws IOException {
        Path target = resolveKey(fileName);
        try {
            Files.move(tempFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException atomicFailed) {
            Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    @Override
    public InputStream load(String key) throws IOException {
        Path file = resolveKey(key);
        if (!Files.exists(file)) {
            throw new CustomException("备份文件不存在或已被清理");
        }
        return Files.newInputStream(file, StandardOpenOption.READ);
    }

    @Override
    public boolean delete(String key) {
        try {
            return Files.deleteIfExists(resolveKey(key));
        } catch (IOException e) {
            return false;
        }
    }

    // key 必须是纯文件名：拦截路径分隔符与穿越，且规范化后必须仍落在备份目录内
    private Path resolveKey(String key) {
        if (key == null || key.isBlank()
                || key.contains("/") || key.contains("\\") || key.contains("..")) {
            throw new CustomException("非法的备份存储 key");
        }
        Path file = root.resolve(key).normalize();
        if (!file.startsWith(root)) {
            throw new CustomException("非法的备份存储 key");
        }
        return file;
    }
}
