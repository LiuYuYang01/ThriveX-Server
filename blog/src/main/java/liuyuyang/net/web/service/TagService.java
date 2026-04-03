package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.tag.TagFilterDTO;
import liuyuyang.net.dto.tag.TagFormDTO;
import liuyuyang.net.model.Tag;
import liuyuyang.net.vo.tag.TagVO;

import java.util.List;

public interface TagService extends IService<Tag> {
    void addTagData(TagFormDTO tagFormDTO);

    void delTagData(Integer id);

    void batchDelTagData(List<Integer> ids);

    void editTagData(TagFormDTO tagFormDTO);

    TagVO getTagData(Integer id);

    Page<TagVO> getTagList(TagFilterDTO tagFilterDTO);
}
