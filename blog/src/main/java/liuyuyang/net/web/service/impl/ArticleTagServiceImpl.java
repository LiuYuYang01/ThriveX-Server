package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.model.ArticleTag;
import liuyuyang.net.web.mapper.ArticleTagMapper;
import liuyuyang.net.web.service.ArticleTagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
