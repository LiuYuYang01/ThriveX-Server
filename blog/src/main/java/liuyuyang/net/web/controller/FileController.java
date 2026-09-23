package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
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
import liuyuyang.net.web.service.FileService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 统一文件上传
 *
 * @author laifeng
 * @date 2024/12/14
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@Transactional
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping
    @Operation(summary = "文件上传")
    public Result<FileUploadVO> addFileData(
            @Parameter(description = "业务相对目录", required = true) @RequestParam String dir,
            @Parameter(description = "待上传文件", required = true) @RequestParam MultipartFile[] files) throws IOException {
        FileUploadVO data = fileService.addFileData(dir, files);
        return Result.success("文件上传成功：", data);
    }

    @DeleteMapping
    @Operation(summary = "删除文件")
    public Result<String> delFileData(
            @Parameter(description = "文件 URL 或 key", required = true) @RequestParam String filePath) {
        fileService.delFileData(filePath);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件")
    public Result<String> batchDelFileData(@RequestBody @Valid FileBatchDeleteFormDTO dto) {
        fileService.batchDelFileData(dto);
        return Result.success();
    }

    @GetMapping("/info")
    @Operation(summary = "获取文件信息")
    public Result<FileInfoVO> getFileData(
            @Parameter(description = "文件 URL 或 key", required = true) @RequestParam String filePath) {
        return Result.success(fileService.getFileData(filePath));
    }

    @GetMapping("/list")
    @Operation(summary = "获取指定目录中的文件")
    public Result<Map<String, Object>> getFileList(FileFilterDTO fileFilterDTO) {
        Page<FileListItemVO> list = fileService.getFileList(fileFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取文件目录树")
    public Result<FileTreeVO> getFileTreeData() {
        return Result.success(fileService.getFileTreeData());
    }

    @PostMapping("/dir")
    @Operation(summary = "新增目录")
    public Result<FileDirCreateVO> addFileDirData(@RequestBody @Valid FileDirCreateFormDTO dto) throws IOException {
        return Result.success(fileService.addFileDirData(dto));
    }

    @PatchMapping("/dir")
    @Operation(summary = "重命名目录")
    public Result<FileDirRenameVO> renameFileDirData(@RequestBody @Valid FileDirRenameFormDTO dto) {
        return Result.success(fileService.renameFileDirData(dto));
    }

    @DeleteMapping("/dir")
    @Operation(summary = "删除目录")
    public Result<FileDirDeleteVO> delFileDirData(@RequestBody @Valid FileDirDeleteFormDTO dto) {
        return Result.success(fileService.delFileDirData(dto));
    }

    @PostMapping("/compress")
    @Operation(summary = "图片瘦身（七牛 pfop 异步）")
    public Result<FileCompressVO> compressFileData(@RequestBody @Valid FileCompressFormDTO dto) {
        return Result.success(fileService.compressFileData(dto));
    }

    @GetMapping("/compress/task/{taskId}")
    @Operation(summary = "查询单个瘦身任务状态")
    public Result<FileCompressItemVO> queryCompressTask(
            @Parameter(description = "七牛 pfop persistentId", required = true) @PathVariable String taskId) {
        return Result.success(fileService.queryCompressTask(taskId));
    }

    @PostMapping("/compress/tasks")
    @Operation(summary = "批量查询瘦身任务状态")
    public Result<List<FileCompressItemVO>> queryCompressTasks(@RequestBody @Valid FileCompressTaskQueryDTO dto) {
        return Result.success(fileService.queryCompressTasks(dto));
    }
}
