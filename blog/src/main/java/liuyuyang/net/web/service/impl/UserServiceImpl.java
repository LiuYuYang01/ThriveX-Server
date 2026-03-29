package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.JwtUtils;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.user.EditUserPassDTO;
import liuyuyang.net.dto.user.EditUserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.User;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.web.mapper.UserMapper;
import liuyuyang.net.web.mapper.UserTokenMapper;
import liuyuyang.net.web.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserTokenMapper userTokenMapper;

    @Override
    public void editUserData(EditUserInfoDTO user) {
        User data = userMapper.selectById(user.getId());
        BeanUtils.copyProperties(user, data);
        userMapper.updateById(data);
    }

    @Override
    public User getUserInfo(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new CustomException(401, "请先登录");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        LambdaQueryWrapper<UserToken> w = new LambdaQueryWrapper<>();
        w.eq(UserToken::getToken, token);
        List<UserToken> userTokens = userTokenMapper.selectList(w);
        if (userTokens == null || userTokens.isEmpty()) {
            throw new CustomException(401, "该账号已在另一台设备登录");
        }

        // 校验token是否有效
        try {
            JwtUtils.parseJWT(token);
        } catch (Exception e) {
            throw new CustomException(401, "无效或过期的 Token");
        }

        Integer id = userTokens.get(0).getUid();
        return userMapper.selectById(id);
    }

    @Override
    public Map<String, Object> login(UserLoginDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        queryWrapper.eq(User::getPassword, DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));

        User user = userMapper.selectOne(queryWrapper);
        if (user == null)
            throw new CustomException(400, "用户名或密码错误");

        Map<String, Object> result = new HashMap<>();
        String token = JwtUtils.createJWT(result);
        result.put("token", token);

        // 先删除用户旧的token
        LambdaQueryWrapper<UserToken> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(UserToken::getUid, user.getId());
        userTokenMapper.delete(userLambdaQueryWrapper);
        // 再存储用户当前的token
        UserToken userToken = new UserToken();
        userToken.setUid(user.getId());
        userToken.setToken(token);
        userTokenMapper.insert(userToken);

        return result;
    }

    @Override
    public void editUserPass(EditUserPassDTO data) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, data.getOldUsername());
        queryWrapper.eq(User::getPassword, DigestUtils.md5DigestAsHex(data.getOldPassword().getBytes()));

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new CustomException(400, "用户名或旧密码错误");
        }

        user.setUsername(data.getNewUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(data.getNewPassword().getBytes()));
        userMapper.updateById(user);
    }

    @Override
    public void checkToken() {
        String token = CommonUtils.getHeader("Authorization");
        boolean isCheck = commonUtils.checkToken(token);

        if (!isCheck) {
            // 获取当前HTTP请求的上下文信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            HttpServletResponse response = null;
            if (attributes != null) {
                response = attributes.getResponse();

                if (response != null) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
                }
            }
            throw new CustomException(400, "身份验证失败：无效或过期的Token");
        }
    }
}