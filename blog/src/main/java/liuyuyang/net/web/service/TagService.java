package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Tag;
import liuyuyang.net.dto.PageDTO;

public interface TagService extends IService<Tag> {
    boolean addTagData(Tag tag);

    Page<Tag> getTagList(PageDTO pageDto);
}
