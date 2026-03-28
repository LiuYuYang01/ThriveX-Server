package liuyuyang.net.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrintConfig {
    private final ConfigurableEnvironment environment;

    public PrintConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @EventListener
    public void printConfigurations(ApplicationStartedEvent event) {
         log.info("\n----------------------------------------------------------\n\t\t" +
                        "服务已启动: 欢迎使用 ThriveX 博客管理系统 \n\t\t" +
                        "接口地址: \thttp://localhost:{}/api\n\t\t" +
                        "API文档: \thttp://localhost:{}/doc.html\n\t\t" +
                        "加入项目交流群: liuyuyang2023\n" +
                        "----------------------------------------------------------",
                environment.getProperty("server.port"), environment.getProperty("server.port"));
    }
} 