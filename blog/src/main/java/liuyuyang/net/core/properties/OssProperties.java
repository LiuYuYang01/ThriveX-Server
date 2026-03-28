package liuyuyang.net.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "oss")
@Data
public class OssProperties {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private List<String> dirList;
}
