package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.qiniu.common.QiniuException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.storage.QiniuStorageService;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

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
    private final QiniuStorageService qiniuStorageService;

    public FileController(QiniuStorageService qiniuStorageService) {
        this.qiniuStorageService = qiniuStorageService;
    }

    @PostMapping
    @ApiOperation("文件上传")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<Object> add(@RequestParam(defaultValue = "") String dir, @RequestParam MultipartFile[] files)
            throws IOException {
        if (dir == null || dir.trim().isEmpty())
            throw new CustomException(400, "请指定一个目录");

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            // 校验文件是否为空
            if (file.isEmpty()) {
                throw new CustomException(400, "文件不能为空");
            }

            // 只允许的图片扩展名
            Set<String> allowedExt = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp"));
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            }

            if (!allowedExt.contains(ext)) {
                throw new CustomException(400, "仅支持上传图片类型文件（jpg、jpeg、png、webp）");
            }

            // 只允许的图片 MIME 类型
            Set<String> allowedContentTypes = new HashSet<>(Arrays.asList(
                    "image/jpeg",
                    "image/png",
                    "image/webp"));
            String contentType = file.getContentType();
            if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase())) {
                throw new CustomException(400, "文件类型不合法，仅支持上传图片类型文件");
            }

            // 解码校验，防止伪装成图片的恶意文件
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new CustomException(400, "文件内容不是有效的图片");
            }

            urls.add(qiniuStorageService.upload(dir, file));
        }

        return Result.success("文件上传成功：", urls);
    }

    @DeleteMapping
    @ApiOperation("删除文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@RequestParam String filePath) throws QiniuException {
        boolean delete = qiniuStorageService.deleteByUrl(filePath);
        return Result.status(delete);
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDel(@RequestBody String[] pathList) throws QiniuException {
        for (String url : pathList) {
            boolean delete = qiniuStorageService.deleteByUrl(url);
            if (!delete)
                throw new CustomException("删除文件失败");
        }
        return Result.success();
    }

    @GetMapping("/info")
    @ApiOperation("获取文件信息")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<Map<String, Object>> get(@RequestParam String filePath) throws QiniuException {
        return Result.success(qiniuStorageService.getFileInfo(filePath));
    }

    @GetMapping("/dir")
    @ApiOperation("获取目录列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<List<Map<String, String>>> getDirList() throws QiniuException {
        return Result.success(qiniuStorageService.listDirectories());
    }

    @GetMapping("/list")
    @ApiOperation("获取指定目录中的文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Map<String, Object>> getFileList(
            @RequestParam String dir,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) throws QiniuException {
        if (dir == null || dir.trim().isEmpty())
            throw new CustomException(400, "请指定一个目录");
        return Result.success(qiniuStorageService.listFiles(dir, page, size));
    }
}
