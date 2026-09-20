package liuyuyang.net.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 文档元数据（原 knife4j yml 分组信息改由该 Bean 提供，UI 入口 /swagger-ui.html）
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("ThriveX 在线API文档")
                .description("有些梦虽然遥不可及，但并不是不可能实现!")
                .contact(new Contact()
                        .name("刘宇阳")
                        .email("liuyuyang1024@yeah.net")
                        .url("https://liuyuyang.net"))
                .license(new License().name("Apache 2.0"))
                .termsOfService("https://stackoverflow.com/")
                .version("v4.0"));
    }
}
