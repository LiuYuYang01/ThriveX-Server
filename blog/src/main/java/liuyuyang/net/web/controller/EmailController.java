package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.dto.email.DismissEmailDTO;
import liuyuyang.net.dto.email.WallEmailDTO;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.EmailService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "邮件管理")
@RestController
@RequestMapping("/email")
@Transactional
public class EmailController {
    @Resource
    private EmailService emailService;

    @PostMapping("/dismiss")
    @ApiOperation("驳回通知邮件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> sendDismissEmailData(@RequestBody DismissEmailDTO dismissEmailDTO) {
        emailService.sendDismissEmailData(dismissEmailDTO);
        return Result.success();
    }

    @PostMapping("/reply_wall")
    @ApiOperation("回复留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> sendWallReplyEmailData(@RequestBody WallEmailDTO wallEmailDTO) {
        emailService.sendWallReplyEmailData(wallEmailDTO);
        return Result.success();
    }
}
