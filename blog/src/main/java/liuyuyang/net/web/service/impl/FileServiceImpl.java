package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiniu.common.QiniuException;
import liuyuyang.net.core.config.QiniuStorageConfig;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.file.FileBatchDeleteFormDTO;
import liuyuyang.net.dto.file.FileDirCreateFormDTO;
import liuyuyang.net.dto.file.FileDirDeleteFormDTO;
import liuyuyang.net.dto.file.FileDirRenameFormDTO;
import liuyuyang.net.dto.file.FileFilterDTO;
import liuyuyang.net.enums.file.FileImageExtensionEnum;
import liuyuyang.net.vo.file.FileDirCreateVO;
import liuyuyang.net.vo.file.FileDirDeleteVO;
import liuyuyang.net.vo.file.FileDirRenameVO;
import liuyuyang.net.vo.file.FileInfoVO;
import liuyuyang.net.vo.file.FileListItemVO;
import liuyuyang.net.vo.file.FileTreeVO;
import liuyuyang.net.vo.file.FileUploadVO;
import liuyuyang.net.web.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class FileServiceImpl implements FileService {

    @Resource
    private QiniuStorageConfig qiniuStorageConfig;

    @Resource
    private CommonUtils commonUtils;

    @Override
    public FileUploadVO addFileData(String dir, MultipartFile[] files) throws IOException {
        if (dir == null || dir.trim().isEmpty()) {
            throw new CustomException("请指定一个目录");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            validateImageFile(file);
            urls.add(qiniuStorageConfig.upload(dir, file));
        }

        FileUploadVO vo = new FileUploadVO();
        vo.setUrls(urls);
        return vo;
    }

    /**
     * 与控制器原逻辑一致：扩展名、MIME、解码校验。
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        // 校验文件是否为空
        if (file.isEmpty()) {
            throw new CustomException("文件不能为空");
        }

        // 只允许的图片扩展名
        Set<String> allowedExt = FileImageExtensionEnum.allowedExtensions();
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        }

        if (!allowedExt.contains(ext)) {
            throw new CustomException("仅支持上传图片类型文件（jpg、jpeg、png、webp）");
        }

        // 只允许的图片 MIME 类型
        Set<String> allowedContentTypes = FileImageExtensionEnum.allowedMimeTypes();
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase())) {
            throw new CustomException("文件类型不合法，仅支持上传图片类型文件");
        }

        // 解码校验，防止伪装成图片的恶意文件
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new CustomException("文件内容不是有效的图片");
        }
    }

    @Override
    public void delFileData(String filePath) throws QiniuException {
        qiniuStorageConfig.deleteByUrl(filePath);
    }

    @Override
    public void batchDelFileData(FileBatchDeleteFormDTO dto) throws QiniuException {
        List<String> pathList = dto.getPaths();
        if (pathList == null || pathList.isEmpty()) {
            return;
        }
        for (String url : pathList) {
            boolean delete = qiniuStorageConfig.deleteByUrl(url);
            if (!delete) {
                throw new CustomException("删除文件失败");
            }
        }
    }

    @Override
    public FileInfoVO getFileData(String filePath) throws QiniuException {
        return qiniuStorageConfig.getFileInfo(filePath);
    }

    @Override
    public Page<FileListItemVO> getFileList(FileFilterDTO fileFilterDTO) throws QiniuException {
        if (fileFilterDTO.getDir() == null || fileFilterDTO.getDir().trim().isEmpty()) {
            throw new CustomException("请指定一个目录");
        }

        List<FileListItemVO> all = qiniuStorageConfig.listFileItems(fileFilterDTO.getDir());

        // 不传 pageNum/pageSize 则返回全部（与分类列表一致）
        if (fileFilterDTO.getPageNum() == null || fileFilterDTO.getPageSize() == null) {
            Page<FileListItemVO> result = new Page<>(1, all.size());
            result.setRecords(new ArrayList<>(all));
            result.setTotal(all.size());
            return result;
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNum(Math.max(1, fileFilterDTO.getPageNum()));
        pageDTO.setPageSize(Math.max(1, fileFilterDTO.getPageSize()));
        return commonUtils.getPageData(pageDTO, all);
    }

    @Override
    public FileTreeVO getFileTreeData() throws QiniuException {
        return qiniuStorageConfig.listFileTree();
    }

    @Override
    public FileDirCreateVO addFileDirData(FileDirCreateFormDTO dto) throws IOException {
        String dir = dto.getDir();
        if (dir == null || dir.trim().isEmpty()) {
            throw new CustomException("请指定一个目录");
        }
        return qiniuStorageConfig.createDirectory(dir);
    }

    @Override
    public FileDirRenameVO renameFileDirData(FileDirRenameFormDTO dto) throws QiniuException {
        String fromDir = dto.getFromDir();
        String toDir = dto.getToDir();
        if (fromDir == null || fromDir.trim().isEmpty() || toDir == null || toDir.trim().isEmpty()) {
            throw new CustomException("请指定原目录和新目录");
        }
        return qiniuStorageConfig.renameDirectory(fromDir, toDir);
    }

    @Override
    public FileDirDeleteVO delFileDirData(FileDirDeleteFormDTO dto) throws QiniuException {
        String dir = dto.getDir();
        if (dir == null || dir.trim().isEmpty()) {
            throw new CustomException("请指定一个目录");
        }
        return qiniuStorageConfig.deleteDirectory(dir);
    }
}
