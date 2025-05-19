package liuyuyang.net.common.config;

import liuyuyang.net.common.properties.JwtProperties;
import liuyuyang.net.common.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 对象初始化完成之后的jwt配置
 *
 * @author: laifeng
 */
@AllArgsConstructor
@EnableConfigurationProperties({JwtProperties.class})
@Configuration
public class JwtConfiguration implements SmartInitializingSingleton {
    private final JwtProperties jwtProperties;

    @Override
    public void afterSingletonsInstantiated() {
        JwtUtils.setJwtProperties(jwtProperties);
    }
}