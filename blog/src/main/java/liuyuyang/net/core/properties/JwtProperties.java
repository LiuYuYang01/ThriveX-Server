package liuyuyang.net.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secretKey;
    // 访问令牌有效期（毫秒）
    private long ttl;
    // 刷新令牌有效期（毫秒）
    private long refreshTtl;
    private String tokenName;
}