package liuyuyang.net.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "FileUploadVO", description = "文件上传结果")
public class FileUploadVO {
    @ApiModelProperty(value = "上传成功后的文件访问 URL 列表")
    private List<String> urls;
}
