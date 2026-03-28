package liuyuyang.net.core.listener;

import liuyuyang.net.model.Oss;
import liuyuyang.net.web.service.OssService;
import liuyuyang.net.core.utils.OssUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 将启用的 OSS 配置注册到存储平台
 */

@Slf4j
@Component
@AllArgsConstructor
public class OssStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private final OssService ossService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 确保是 Spring 应用上下文初始化完成的事件
        if (event.getApplicationContext().getParent() == null) {
            // 查询启用的 OSS 配置
            Oss enabledOss = ossService.getEnableOss();
            if (enabledOss != null) {
                // 注册到存储平台
                registerOssToPlatform(enabledOss);
            } else {
                // 没有启用的 OSS 配置,报错
                throw new RuntimeException("没有发现启用的OSS配置");
            }
        }

    }

    private void registerOssToPlatform(Oss oss) {
        OssUtils.registerPlatform(oss);
    }
}