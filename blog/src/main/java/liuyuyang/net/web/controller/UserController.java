package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.dto.user.EditUserPassDTO;
import liuyuyang.net.dto.user.EditUserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.model.User;
import liuyuyang.net.vo.user.AuthorVO;
import liuyuyang.net.vo.user.UserVO;
import liuyuyang.net.web.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
@Transactional
public class UserController {
    @Resource
    private UserService userService;

    @PatchMapping
    @ApiOperation("编辑管理员")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editUserData(@RequestBody EditUserInfoDTO user) {
        userService.editUserData(user);
        return Result.success();
    }

    @GetMapping("/info")
    @ApiOperation("获取当前登录的管理员信息")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    @NoTokenRequired
    public Result<UserVO> getUserData(String token) {
        User user = userService.getUserInfo(token);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }

    @PostMapping("/login")
    @ApiOperation("管理员登录")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<Map<String, Object>> login(@RequestBody UserLoginDTO user) {
        Map<String, Object> result = userService.login(user);
        return Result.success("登录成功", result);
    }

    @PatchMapping("/pass")
    @ApiOperation("修改管理员密码")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    public Result<String> editPass(@RequestBody EditUserPassDTO data) {
        userService.editUserPass(data);
        return Result.success("密码修改成功");
    }

    // 后续删掉
    @GetMapping("/check")
    @ApiOperation("校验当前管理员Token是否有效")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 10)
    public Result<String> checkToken() {
        userService.checkToken();
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/author")
    @ApiOperation("获取作者信息")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 11)
    public Result<AuthorVO> getAuthor() {
        User user = userService.getById(1);
        AuthorVO author = new AuthorVO();
        BeanUtils.copyProperties(user, author);
        return Result.success(author);
    }
}
