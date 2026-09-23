package liuyuyang.net.core.storage;

import liuyuyang.net.vo.file.FileDirCreateVO;
import liuyuyang.net.vo.file.FileDirDeleteVO;
import liuyuyang.net.vo.file.FileDirRenameVO;
import liuyuyang.net.vo.file.FileInfoVO;
import liuyuyang.net.vo.file.FileListItemVO;
import liuyuyang.net.vo.file.FileTreeVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件存储统一抽象：由 {@link StorageServiceRouter} 根据 env_config 的 storage.type 分发到具体实现。
 * <p>
 * 两套实现的 key 语义保持一致：{@code root_dir + 业务相对目录 + UUID + 扩展名}，
 * 业务数据中保存的均为完整可访问 URL，切换存储方式不影响已发布内容。
 */
public interface StorageService {

    /**
     * 上传文件，返回可公开访问的 URL。
     *
     * @param dir 业务相对目录（仅表示基础目录下的子路径）
     */
    String upload(String dir, MultipartFile file) throws IOException;

    /**
     * 按完整访问 URL（或 key）删除文件。
     */
    boolean deleteByUrl(String url);

    /**
     * 查询单个文件元信息，入参可为 URL、路径或 key。
     */
    FileInfoVO getFileInfo(String filePath);

    /**
     * 按目录列举直接子文件（不含子目录内文件），已过滤占位对象，按上传时间降序。
     */
    List<FileListItemVO> listFileItems(String dir);

    /**
     * 返回整个存储的文件目录树。
     */
    FileTreeVO listFileTree();

    /**
     * 创建逻辑目录。
     */
    FileDirCreateVO createDirectory(String dir) throws IOException;

    /**
     * 重命名目录（整棵子树）。
     */
    FileDirRenameVO renameDirectory(String fromDir, String toDir);

    /**
     * 删除目录（要求目录内无真实文件）。
     */
    FileDirDeleteVO deleteDirectory(String dir);
}
