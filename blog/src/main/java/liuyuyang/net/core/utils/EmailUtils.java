package liuyuyang.net.core.utils;

import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Slf4j
@Component
public class EmailUtils {
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private EnvConfigService envConfigService;

    private Map<String, Object> getEmailConfig() {
        EnvConfig envConfig = envConfigService.getByName("email");
        return envConfig.getValue();
    }

    @Async
    public void send(String to, String subject, String template) {
        log.info("开始发送邮件，主题: {}", subject);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            Map<String, Object> config = getEmailConfig();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromEmail = (String) config.get("username");
            String toEmail = (to == null || to.isEmpty()) ? fromEmail : to;

            // 发送邮件
            helper.setFrom(new InternetAddress(fromEmail, "你有新的消息~")); // 发送者
            helper.setTo(toEmail); // 接收者
            helper.setSubject(subject); // 邮件标题
            helper.setText(template, true); // 第二个参数为 true 表示发送 HTML 格式

            mailSender.send(message);
            log.info("邮件发送成功，主题: {}", subject);
        } catch (Exception e) {
            // 记录错误但不打印收件人邮箱等敏感信息，也不泄露异常堆栈细节到 stdout
            log.error("邮件发送失败，主题: {}", subject, e);

            // 如果是认证失败或连接问题，给出具体建议
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Authentication failed")) {
                    log.warn("建议: 检查邮箱用户名和密码是否正确，或者是否需要使用授权码");
                } else if (e.getMessage().contains("Connection refused") || e.getMessage().contains("timeout")) {
                    log.warn("建议: 检查SMTP服务器地址和端口是否正确，网络是否畅通");
                } else if (e.getMessage().contains("SSL") || e.getMessage().contains("TLS")) {
                    log.warn("建议: 检查SSL/TLS配置是否正确");
                }
            }
        }
    }
}
