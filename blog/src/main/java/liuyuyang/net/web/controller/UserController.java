package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.AuditLog;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.dto.user.EditUserPassDTO;
import liuyuyang.net.dto.user.EditUserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.model.User;
import liuyuyang.net.vo.user.AuthorVO;
import liuyuyang.net.vo.user.UserVO;
import liuyuyang.net.web.service.CaptchaService;
import liuyuyang.net.web.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Map;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@Transactional
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private CaptchaService captchaService;

    @PatchMapping
    @Operation(summary = "编辑管理员")
    public Result<String> editUserData(@RequestBody @Valid EditUserInfoDTO user) {
        userService.editUserData(user);
        return Result.success();
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前登录的管理员信息")
    @NoTokenRequired
    public Result<UserVO> getUserData(String token) {
        User user = userService.getUserInfo(token);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    @AuditLog(module = "用户管理", type = "登录", description = "管理员登录")
    @RateLimit(tokens = 5, duration = 60, message = "登录尝试过于频繁，请 60 秒后再试")
    public Result<Map<String, Object>> login(@RequestBody @Valid UserLoginDTO user) {
        captchaService.check(user.getCaptchaToken());
        Map<String, Object> result = userService.login(user);
        return Result.success("登录成功", result);
    }

    @PatchMapping("/pass")
    @Operation(summary = "修改管理员密码")
    public Result<String> editPass(@RequestBody @Valid EditUserPassDTO data) {
        userService.editUserPass(data);
        return Result.success("密码修改成功");
    }

    // 后续删掉
    @GetMapping("/check")
    @Operation(summary = "校验当前管理员Token是否有效")
    public Result<String> checkToken() {
        userService.checkToken();
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/author")
    @Operation(summary = "获取作者信息")
    public Result<AuthorVO> getAuthor() {
        User user = userService.getById(1);
        AuthorVO author = new AuthorVO();
        BeanUtils.copyProperties(user, author);
        return Result.success(author);
    }
}
