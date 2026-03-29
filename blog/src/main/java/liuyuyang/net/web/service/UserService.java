package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.user.EditPassDTO;
import liuyuyang.net.dto.user.UserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.User;

import java.util.Map;

public interface UserService extends IService<User> {
    User get(Integer id);

    /**
     * 根据请求头中的 Authorization（Bearer token）解析当前登录用户
     */
    User getByToken(String token);

    void edit(UserInfoDTO data);

    Map<String, Object> login(UserLoginDTO user);

    void editPass(EditPassDTO data);

    void checkToken();
}
