package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.qiniu.common.QiniuException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.file.FileBatchDeleteFormDTO;
import liuyuyang.net.dto.file.FileDirCreateFormDTO;
import liuyuyang.net.dto.file.FileDirDeleteFormDTO;
import liuyuyang.net.dto.file.FileDirRenameFormDTO;
import liuyuyang.net.dto.file.FileFilterDTO;
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

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * 统一文件上传
 *
 * @author laifeng
 * @date 2024/12/14
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
@Transactional
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping
    @ApiOperation("文件上传")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<FileUploadVO> addFileData(
            @ApiParam(value = "业务相对目录", required = true) @RequestParam String dir,
            @ApiParam(value = "待上传文件", required = true) @RequestParam MultipartFile[] files) throws IOException {
        FileUploadVO data = fileService.addFileData(dir, files);
        return Result.success("文件上传成功：", data);
    }

    @DeleteMapping
    @ApiOperation("删除文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delFileData(
            @ApiParam(value = "文件 URL 或 key", required = true) @RequestParam String filePath) throws QiniuException {
        fileService.delFileData(filePath);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelFileData(@RequestBody @Valid FileBatchDeleteFormDTO dto) throws QiniuException {
        fileService.batchDelFileData(dto);
        return Result.success();
    }

    @GetMapping("/info")
    @ApiOperation("获取文件信息")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<FileInfoVO> getFileData(
            @ApiParam(value = "文件 URL 或 key", required = true) @RequestParam String filePath) throws QiniuException {
        return Result.success(fileService.getFileData(filePath));
    }

    @GetMapping("/list")
    @ApiOperation("获取指定目录中的文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Map<String, Object>> getFileList(FileFilterDTO fileFilterDTO) throws QiniuException {
        Page<FileListItemVO> list = fileService.getFileList(fileFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @GetMapping("/tree")
    @ApiOperation("获取文件目录树")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<FileTreeVO> getFileTreeData() throws QiniuException {
        return Result.success(fileService.getFileTreeData());
    }

    @PostMapping("/dir")
    @ApiOperation("新增目录")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<FileDirCreateVO> addFileDirData(@RequestBody @Valid FileDirCreateFormDTO dto) throws IOException {
        return Result.success(fileService.addFileDirData(dto));
    }

    @PatchMapping("/dir")
    @ApiOperation("重命名目录")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<FileDirRenameVO> renameFileDirData(@RequestBody @Valid FileDirRenameFormDTO dto) throws QiniuException {
        return Result.success(fileService.renameFileDirData(dto));
    }

    @DeleteMapping("/dir")
    @ApiOperation("删除目录")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    public Result<FileDirDeleteVO> delFileDirData(@RequestBody @Valid FileDirDeleteFormDTO dto) throws QiniuException {
        return Result.success(fileService.delFileDirData(dto));
    }
}
