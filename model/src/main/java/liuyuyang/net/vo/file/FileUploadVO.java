package liuyuyang.net.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "FileUploadVO", description = "文件上传结果")
public class FileUploadVO {
    @Schema(description = "上传成功后的文件访问 URL 列表")
    private List<String> urls;
}
