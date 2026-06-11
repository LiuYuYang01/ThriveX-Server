package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiniu.common.QiniuException;
import liuyuyang.net.dto.file.FileBatchDeleteFormDTO;
import liuyuyang.net.dto.file.FileCompressFormDTO;
import liuyuyang.net.dto.file.FileCompressTaskQueryDTO;
import liuyuyang.net.dto.file.FileDirCreateFormDTO;
import liuyuyang.net.dto.file.FileDirDeleteFormDTO;
import liuyuyang.net.dto.file.FileDirRenameFormDTO;
import liuyuyang.net.dto.file.FileFilterDTO;
import liuyuyang.net.vo.file.FileCompressItemVO;
import liuyuyang.net.vo.file.FileCompressVO;
import liuyuyang.net.vo.file.FileDirCreateVO;
import liuyuyang.net.vo.file.FileDirDeleteVO;
import liuyuyang.net.vo.file.FileDirRenameVO;
import liuyuyang.net.vo.file.FileInfoVO;
import liuyuyang.net.vo.file.FileListItemVO;
import liuyuyang.net.vo.file.FileTreeVO;
import liuyuyang.net.vo.file.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    FileUploadVO addFileData(String dir, MultipartFile[] files) throws IOException;

    void delFileData(String filePath) throws QiniuException;

    void batchDelFileData(FileBatchDeleteFormDTO dto) throws QiniuException;

    FileInfoVO getFileData(String filePath) throws QiniuException;

    Page<FileListItemVO> getFileList(FileFilterDTO fileFilterDTO) throws QiniuException;

    FileTreeVO getFileTreeData() throws QiniuException;

    FileDirCreateVO addFileDirData(FileDirCreateFormDTO dto) throws IOException;

    FileDirRenameVO renameFileDirData(FileDirRenameFormDTO dto) throws QiniuException;

    FileDirDeleteVO delFileDirData(FileDirDeleteFormDTO dto) throws QiniuException;

    FileCompressVO compressFileData(FileCompressFormDTO dto);

    FileCompressItemVO queryCompressTask(String taskId);

    List<FileCompressItemVO> queryCompressTasks(FileCompressTaskQueryDTO dto);
}
