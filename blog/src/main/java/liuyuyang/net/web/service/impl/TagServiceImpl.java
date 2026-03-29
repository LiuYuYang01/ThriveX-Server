package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.mapper.TagMapper;
import liuyuyang.net.model.Tag;
import liuyuyang.net.web.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Resource
    private TagMapper tagMapper;

    @Override
    public boolean addTagData(Tag tag) {
        LambdaQueryWrapper<Tag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Tag::getName, tag.getName());
        Tag data = tagMapper.selectOne(lambdaQueryWrapper);
        if (data != null) throw new RuntimeException("该标签已存在");

        return this.save(tag);
    }

    @Override
    public Page<Tag> getTagList(PageDTO pageDTO) {
        List<Tag> data = tagMapper.staticArticleCount();

        // 不传分页参数时返回全部（page/size 任意一个未传则全量）
        if (pageDTO == null || pageDTO.getPageNum() == null || pageDTO.getPageSize() == null) {
            Page<Tag> result = new Page<>(1, data.size());
            result.setRecords(new ArrayList<>(data));
            result.setTotal((long) data.size());
            return result;
        }

        if (pageDTO.getPageNum() <= 0 || pageDTO.getPageSize() <= 0) {
            throw new CustomException(400, "分页参数 page/size 必须大于 0");
        }

        // 手动分页（数据源为统计查询结果）
        Page<Tag> result = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        int start = (int) ((pageDTO.getPageNum() - 1L) * pageDTO.getPageSize());
        int end = Math.min(start + pageDTO.getPageSize(), data.size());
        List<Tag> records = start >= data.size() ? new ArrayList<>() : data.subList(start, end);
        result.setRecords(new ArrayList<>(records));
        result.setTotal((long) data.size());
        return result;
    }
}