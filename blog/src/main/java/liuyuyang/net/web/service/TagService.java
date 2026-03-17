package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {
    void addTagData(Tag tag);

    Page<Tag> list(Integer page, Integer size);

    List<Tag> staticArticleCount();
}
