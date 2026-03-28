package liuyuyang.net.core.utils;

import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;

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
        System.out.println("=== 开始发送邮件 ===");
        System.out.println("收件人: " + to);
        System.out.println("主题: " + subject);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            Map<String, Object> config = getEmailConfig();
            System.out.println("邮件配置: " + config);

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromEmail = (String) config.get("username");
            String toEmail = (to == null || to.isEmpty()) ? fromEmail : to;

            System.out.println("发件人: " + fromEmail);
            System.out.println("实际收件人: " + toEmail);

            // 发送邮件
            helper.setFrom(new InternetAddress(fromEmail, "你有新的消息~")); // 发送者
            helper.setTo(toEmail); // 接收者
            helper.setSubject(subject); // 邮件标题
            helper.setText(template, true); // 第二个参数为 true 表示发送 HTML 格式

            System.out.println("正在发送邮件...");
            mailSender.send(message);
            System.out.println("邮件发送成功！");
        } catch (Exception e) {
            System.err.println("=== 邮件发送失败 ===");
            System.err.println("错误类型: " + e.getClass().getSimpleName());
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();

            // 如果是认证失败或连接问题，给出具体建议
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Authentication failed")) {
                    System.err.println("建议: 检查邮箱用户名和密码是否正确，或者是否需要使用授权码");
                } else if (e.getMessage().contains("Connection refused") || e.getMessage().contains("timeout")) {
                    System.err.println("建议: 检查SMTP服务器地址和端口是否正确，网络是否畅通");
                } else if (e.getMessage().contains("SSL") || e.getMessage().contains("TLS")) {
                    System.err.println("建议: 检查SSL/TLS配置是否正确");
                }
            }
        }
    }
}
