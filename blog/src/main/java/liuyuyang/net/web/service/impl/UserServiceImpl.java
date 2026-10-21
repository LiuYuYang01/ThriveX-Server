package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.JwtUtils;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.core.utils.PasswordUtils;
import liuyuyang.net.dto.user.EditUserPassDTO;
import liuyuyang.net.dto.user.EditUserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.model.User;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.web.mapper.EnvConfigMapper;
import liuyuyang.net.web.mapper.UserMapper;
import liuyuyang.net.web.mapper.UserTokenMapper;
import liuyuyang.net.web.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
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
    @Resource
    private EnvConfigMapper envConfigMapper;

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

        Integer uid = JwtUtils.parseUid(token);
        if (uid == null) {
            throw new CustomException(401, "无效或过期的 Token");
        }
        return userMapper.selectById(uid);
    }

    @Override
    public Map<String, Object> login(UserLoginDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());

        // 只按用户名查询，密码校验交给 PasswordUtils（避免 MD5/BCrypt 差异导致无法命中）
        User user = userMapper.selectOne(queryWrapper);
        if (user == null || !PasswordUtils.matches(userDTO.getPassword(), user.getPassword())) {
            throw new CustomException("用户名或密码错误");
        }

        // 渐进升级：存量 MD5 哈希登录成功后，用明文重新编码为 BCrypt 写回
        if (PasswordUtils.needsRehash(user.getPassword())) {
            user.setPassword(PasswordUtils.encode(userDTO.getPassword()));
            userMapper.updateById(user);
        }

        // 签发 access token（短有效期，携带 uid）与 refresh token（长有效期）
        String accessToken = JwtUtils.createAccessToken(user.getId());
        String refreshToken = JwtUtils.createRefreshToken(user.getId());

        // 先删除用户旧的 token，保证单设备登录（旧 token 作废）
        LambdaQueryWrapper<UserToken> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(UserToken::getUid, user.getId());
        userTokenMapper.delete(userLambdaQueryWrapper);

        // 存储当前 access token
        UserToken userToken = new UserToken();
        userToken.setUid(user.getId());
        userToken.setToken(accessToken);
        userTokenMapper.insert(userToken);

        Map<String, Object> result = new HashMap<>();
        result.put("token", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    @Override
    public Map<String, Object> refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new CustomException(401, "缺少刷新令牌");
        }
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        // 校验 refresh token 的签名与有效期
        Integer uid;
        try {
            uid = JwtUtils.parseUid(refreshToken);
        } catch (Exception e) {
            throw new CustomException(401, "刷新令牌无效或已过期");
        }
        if (uid == null) {
            throw new CustomException(401, "刷新令牌无效");
        }

        // 确认用户仍存在
        User user = userMapper.selectById(uid);
        if (user == null) {
            throw new CustomException(401, "用户不存在");
        }

        // 签发新的 access token（保留现有 refresh token，避免无限续期；如需轮换 refresh 可在此扩展）
        String accessToken = JwtUtils.createAccessToken(uid);

        // 更新 user_token 表中的 token（旧 access token 作废，保持单设备）
        LambdaQueryWrapper<UserToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserToken::getUid, uid);
        UserToken existing = userTokenMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setToken(accessToken);
            userTokenMapper.updateById(existing);
        } else {
            UserToken userToken = new UserToken();
            userToken.setUid(uid);
            userToken.setToken(accessToken);
            userTokenMapper.insert(userToken);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("token", accessToken);
        return result;
    }

    @Override
    public void editUserPass(EditUserPassDTO data) {
        if (data.getOldUsername() == null) throw new CustomException("请输入旧用户名");
        if (data.getNewUsername() == null) throw new CustomException("请输入新用户名");
        if (data.getNewPassword() == null) throw new CustomException("请输入新密码");

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, data.getOldUsername());

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new CustomException("用户名或旧密码错误");
        }

        String oldPassword = data.getOldPassword();
        if (oldPassword == null || oldPassword.isEmpty()) {
            // 初始化阶段允许直接设置新密码，初始化完成后必须校验旧密码
            if (isSystemInit()) throw new CustomException("请输入旧密码");
        } else {
            // 用 PasswordUtils 校验旧密码（兼容存量 MD5）
            if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
                throw new CustomException("用户名或旧密码错误");
            }
        }

        user.setUsername(data.getNewUsername());
        user.setPassword(PasswordUtils.encode(data.getNewPassword()));
        userMapper.updateById(user);
    }

    // 系统是否已完成初始化
    private boolean isSystemInit() {
        LambdaQueryWrapper<EnvConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnvConfig::getName, "is_system_init");
        EnvConfig envConfig = envConfigMapper.selectOne(queryWrapper);
        if (envConfig == null || envConfig.getValue() == null) return false;

        Object value = envConfig.getValue().get("value");
        if (value instanceof Boolean) return (Boolean) value;
        return value != null && Boolean.parseBoolean(String.valueOf(value));
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
            throw new CustomException("身份验证失败：无效或过期的Token");
        }
    }
}
