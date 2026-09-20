package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.dto.email.DismissEmailDTO;
import liuyuyang.net.dto.email.WallEmailDTO;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.EmailService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Tag(name = "邮件管理")
@RestController
@RequestMapping("/email")
@Transactional
public class EmailController {
    @Resource
    private EmailService emailService;

    @PostMapping("/dismiss")
    @Operation(summary = "驳回通知邮件")
    public Result<String> sendDismissEmailData(@RequestBody DismissEmailDTO dismissEmailDTO) {
        emailService.sendDismissEmailData(dismissEmailDTO);
        return Result.success();
    }

    @PostMapping("/reply_wall")
    @Operation(summary = "回复留言")
    public Result<String> sendWallReplyEmailData(@RequestBody WallEmailDTO wallEmailDTO) {
        emailService.sendWallReplyEmailData(wallEmailDTO);
        return Result.success();
    }
}
