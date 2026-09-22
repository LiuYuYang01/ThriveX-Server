package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.web.mapper.WallCateMapper;
import liuyuyang.net.model.WallCate;
import liuyuyang.net.web.service.WallCateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WallCateServiceImpl extends ServiceImpl<WallCateMapper, WallCate> implements WallCateService {

}
