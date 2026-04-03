package liuyuyang.net.web.service.impl;

import liuyuyang.net.core.utils.EmailUtils;
import liuyuyang.net.dto.email.DismissEmailDTO;
import liuyuyang.net.dto.email.WallEmailDTO;
import liuyuyang.net.web.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    @Resource
    private EmailUtils emailUtils;
    @Resource
    private TemplateEngine templateEngine;

    @Override
    public void sendDismissEmailData(DismissEmailDTO email) {
        // 处理邮件模板
        Context context = new Context();
        context.setVariable("type", email.getType());
        context.setVariable("recipient", email.getRecipient());
        context.setVariable("time", email.getTime());
        context.setVariable("content", email.getContent());
        context.setVariable("url", email.getUrl());
        String template = templateEngine.process("dismiss_email", context);

        emailUtils.send(email.getTo() != null ? email.getTo() : null, email.getSubject(), template);
    }

    @Override
    public void sendWallReplyEmailData(WallEmailDTO email) {
        // 处理邮件模板
        Context context = new Context();
        context.setVariable("recipient", email.getRecipient());
        context.setVariable("time", email.getTime());
        context.setVariable("your_content", email.getYour_content());
        context.setVariable("reply_content", email.getReply_content());
        context.setVariable("url", email.getUrl());
        String template = templateEngine.process("wall_email", context);

        emailUtils.send(email.getTo() != null ? email.getTo() : null, "您有新的消息~", template);
    }
}
