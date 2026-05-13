package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.tag.TagFilterDTO;
import liuyuyang.net.dto.tag.TagFormDTO;
import liuyuyang.net.model.Tag;
import liuyuyang.net.vo.article.ArticleVO;
import liuyuyang.net.vo.tag.TagVO;
import liuyuyang.net.web.mapper.TagMapper;
import liuyuyang.net.web.service.ArticleService;
import liuyuyang.net.web.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Resource
    private TagMapper tagMapper;
    @Resource
    private ArticleService articleService;

    @Override
    public void addTagData(TagFormDTO tagFormDTO) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagFormDTO, tag);
        tag.setId(null);
        LambdaQueryWrapper<Tag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Tag::getName, tag.getName());
        Tag data = tagMapper.selectOne(lambdaQueryWrapper);
        if (data != null) {
            throw new CustomException("该标签已存在");
        }
        this.save(tag);
    }

    @Override
    public void delTagData(Integer id) {
        int affected = tagMapper.deleteById(id);
        if (affected == 0) {
            throw new CustomException("该标签不存在");
        }
    }

    @Override
    public void batchDelTagData(List<Integer> ids) {
        // 与 ArticleController#batchDelArticleData 一致：空列表在 Service 内短路
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }

    @Override
    public void editTagData(TagFormDTO tagFormDTO) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagFormDTO, tag);
        this.updateById(tag);
    }

    @Override
    public TagVO getTagData(Integer id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new CustomException("该标签不存在");
        }
        TagVO vo = toVO(tag);
        // 与列表一致：附带文章数（来自统计查询结果）
        tagMapper.staticArticleCount().stream()
                .filter(t -> id.equals(t.getId()))
                .findFirst()
                .ifPresent(t -> vo.setCount(t.getCount()));
        return vo;
    }

    @Override
    public Page<TagVO> getTagList(TagFilterDTO tagFilterDTO) {
        List<Tag> data = tagMapper.staticArticleCount();

        // 不传分页参数时返回全部（page/size 任意一个未传则全量）
        if (tagFilterDTO == null || tagFilterDTO.getPageNum() == null || tagFilterDTO.getPageSize() == null) {
            Page<TagVO> result = new Page<>(1, data.size());
            result.setRecords(data.stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
            result.setTotal((long) data.size());
            return result;
        }

        if (tagFilterDTO.getPageNum() <= 0 || tagFilterDTO.getPageSize() <= 0) {
            throw new CustomException("分页参数 page/size 必须大于 0");
        }

        // 手动分页（数据源为统计查询结果）
        Page<TagVO> result = new Page<>(tagFilterDTO.getPageNum(), tagFilterDTO.getPageSize());
        int start = (int) ((tagFilterDTO.getPageNum() - 1L) * tagFilterDTO.getPageSize());
        int end = Math.min(start + tagFilterDTO.getPageSize(), data.size());
        List<Tag> records = start >= data.size() ? new ArrayList<>() : data.subList(start, end);
        result.setRecords(records.stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
        result.setTotal((long) data.size());
        return result;
    }

    @Override
    public Page<ArticleVO> getTagArticleList(Integer tagId, PageDTO pageDTO) {
        PageDTO dto = pageDTO != null ? pageDTO : new PageDTO();
        return articleService.getTagArticleList(tagId, dto);
    }

    private TagVO toVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}
