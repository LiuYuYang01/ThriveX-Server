package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiniu.common.QiniuException;
import com.qiniu.processing.OperationStatus;
import liuyuyang.net.core.config.QiniuStorageConfig;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.service.CompressTaskStore;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.core.utils.ImagePfopUtils;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.file.FileBatchDeleteFormDTO;
import liuyuyang.net.dto.file.FileCompressFormDTO;
import liuyuyang.net.dto.file.FileCompressTaskQueryDTO;
import liuyuyang.net.dto.file.FileDirCreateFormDTO;
import liuyuyang.net.dto.file.FileDirDeleteFormDTO;
import liuyuyang.net.dto.file.FileDirRenameFormDTO;
import liuyuyang.net.dto.file.FileFilterDTO;
import liuyuyang.net.enums.file.FileImageExtensionEnum;
import liuyuyang.net.vo.file.FileCompressItemVO;
import liuyuyang.net.vo.file.FileCompressVO;
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

    @Resource
    private CompressTaskStore compressTaskStore;

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
        if (file.isEmpty()) {
            throw new CustomException("文件不能为空");
        }

        Set<String> allowedExt = FileImageExtensionEnum.allowedExtensions();
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        }

        if (!allowedExt.contains(ext)) {
            throw new CustomException("仅支持上传图片类型文件（jpg、jpeg、png、webp）");
        }

        Set<String> allowedContentTypes = FileImageExtensionEnum.allowedMimeTypes();
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase())) {
            throw new CustomException("文件类型不合法，仅支持上传图片类型文件");
        }

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

    @Override
    public FileCompressVO compressFileData(FileCompressFormDTO dto) {
        List<String> pathList = dto.getPaths();
        if (pathList == null || pathList.isEmpty()) {
            throw new CustomException("文件路径列表不能为空");
        }

        String mode = dto.getMode();
        List<FileCompressItemVO> items = new ArrayList<>();
        int successCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        for (String filePath : pathList) {
            FileCompressItemVO item = submitCompressTask(filePath, mode);
            items.add(item);
            if ("processing".equals(item.getStatus())) {
                // 异步任务不计入终态统计
            } else if ("skipped".equals(item.getStatus())) {
                skippedCount++;
            } else if ("failed".equals(item.getStatus())) {
                failedCount++;
            } else if ("success".equals(item.getStatus())) {
                successCount++;
            }
        }

        FileCompressVO vo = new FileCompressVO();
        vo.setItems(items);
        vo.setSuccessCount(successCount);
        vo.setSkippedCount(skippedCount);
        vo.setFailedCount(failedCount);
        vo.setTotalSavedBytes(0L);
        return vo;
    }

    @Override
    public FileCompressItemVO queryCompressTask(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new CustomException("任务 ID 不能为空");
        }

        CompressTaskStore.Context context = compressTaskStore.get(taskId);
        if (context == null) {
            throw new CustomException("任务不存在或已过期，请重新提交瘦身");
        }

        try {
            OperationStatus status = qiniuStorageConfig.queryPfopStatus(taskId);
            int code = status.code;
            if (code == 1 || code == 2) {
                return compressTaskStore.toProcessingItem(context, taskId);
            }
            if (code == 3) {
                compressTaskStore.remove(taskId);
                qiniuStorageConfig.deleteKeyQuietly(context.getTmpKey());
                return buildFailedItem(context, taskId, "七牛 pfop 处理失败：" + status.desc);
            }

            return finalizeTask(context, taskId);
        } catch (QiniuException e) {
            return buildFailedItem(context, taskId, "查询任务失败：" + e.getMessage());
        }
    }

    @Override
    public List<FileCompressItemVO> queryCompressTasks(FileCompressTaskQueryDTO dto) {
        List<FileCompressItemVO> items = new ArrayList<>();
        for (String taskId : dto.getTaskIds()) {
            items.add(queryCompressTask(taskId));
        }
        return items;
    }

    private FileCompressItemVO submitCompressTask(String filePath, String mode) {
        FileCompressItemVO item = new FileCompressItemVO();
        item.setPath(filePath);
        try {
            FileInfoVO info = qiniuStorageConfig.getFileInfo(filePath);
            item.setName(info.getName());
            item.setBeforeSize(info.getSize());

            String ext = ImagePfopUtils.extractExtension(info.getName());
            long beforeSize = info.getSize() == null ? 0L : info.getSize();
            ImagePfopUtils.Plan plan = ImagePfopUtils.resolvePlan(ext, beforeSize, mode);
            if (plan.isSkip()) {
                item.setStatus("skipped");
                item.setAfterSize(beforeSize);
                item.setMessage(plan.getReason());
                return item;
            }

            String key = info.getPath();
            String tmpKey = ImagePfopUtils.buildTmpKey(key);
            String persistentId = qiniuStorageConfig.submitCompressPfop(key, plan.getFops());

            CompressTaskStore.Context context = new CompressTaskStore.Context(
                    filePath, info.getName(), key, tmpKey, beforeSize);
            compressTaskStore.put(persistentId, context);
            return compressTaskStore.toProcessingItem(context, persistentId);
        } catch (QiniuException e) {
            item.setStatus("failed");
            item.setMessage("提交 pfop 失败：" + e.getMessage());
            return item;
        } catch (Exception e) {
            item.setStatus("failed");
            item.setMessage("提交瘦身任务失败：" + e.getMessage());
            return item;
        }
    }

    private FileCompressItemVO finalizeTask(CompressTaskStore.Context context, String taskId) {
        try {
            FileInfoVO updated = qiniuStorageConfig.finalizeCompressPfop(
                    context.getKey(), context.getTmpKey(), context.getBeforeSize());
            compressTaskStore.remove(taskId);

            long beforeSize = context.getBeforeSize();
            long afterSize = updated.getSize() == null ? 0L : updated.getSize();
            FileCompressItemVO item = new FileCompressItemVO();
            item.setPath(context.getPath());
            item.setName(context.getName());
            item.setTaskId(taskId);
            item.setStatus("success");
            item.setBeforeSize(beforeSize);
            item.setAfterSize(afterSize);
            item.setSavedPercent(ImagePfopUtils.calcSavedPercent(beforeSize, afterSize));
            item.setMessage(String.format("体积减少 %s", ImagePfopUtils.formatSavedBytes(beforeSize - afterSize)));
            return item;
        } catch (CustomException e) {
            compressTaskStore.remove(taskId);
            FileCompressItemVO item = new FileCompressItemVO();
            item.setPath(context.getPath());
            item.setName(context.getName());
            item.setTaskId(taskId);
            item.setStatus("skipped");
            item.setBeforeSize(context.getBeforeSize());
            item.setAfterSize(context.getBeforeSize());
            item.setMessage(e.getMessage());
            return item;
        } catch (QiniuException e) {
            compressTaskStore.remove(taskId);
            return buildFailedItem(context, taskId, "覆盖原文件失败：" + e.getMessage());
        }
    }

    private FileCompressItemVO buildFailedItem(CompressTaskStore.Context context, String taskId, String message) {
        FileCompressItemVO item = new FileCompressItemVO();
        item.setPath(context.getPath());
        item.setName(context.getName());
        item.setTaskId(taskId);
        item.setStatus("failed");
        item.setBeforeSize(context.getBeforeSize());
        item.setMessage(message);
        return item;
    }
}
