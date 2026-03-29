package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.user.EditUserPassDTO;
import liuyuyang.net.dto.user.EditUserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.User;

import java.util.Map;

public interface UserService extends IService<User> {
    User getUserInfo(String token);

    void editUserData(EditUserInfoDTO data);

    Map<String, Object> login(UserLoginDTO user);

    void editUserPass(EditUserPassDTO data);

    void checkToken();
}
