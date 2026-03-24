package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.user.EditPassDTO;
import liuyuyang.net.dto.user.UserDTO;
import liuyuyang.net.dto.user.UserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.User;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.user.UserFilterVo;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    User get(Integer id);
    void add(UserDTO data);
    void del(Integer id);
    void delBatch(List<Integer> ids);
    void edit(UserInfoDTO data);
    List<User> list(UserFilterVo filterVo);
    Page<User> paging(UserFilterVo filterVo, PageVo pageVo);
    Map<String, Object> login(UserLoginDTO user);
    void editPass(EditPassDTO data);
    void checkToken();
}
