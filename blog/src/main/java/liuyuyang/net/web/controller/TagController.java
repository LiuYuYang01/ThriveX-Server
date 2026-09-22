package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.tag.TagFilterDTO;
import liuyuyang.net.dto.tag.TagFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.article.ArticleVO;
import liuyuyang.net.vo.tag.TagVO;
import liuyuyang.net.web.service.TagService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/tag")
@Transactional
@Validated
public class TagController {
    @Resource
    private TagService tagService;

    @PostMapping
    @Operation(summary = "新增标签")
    public Result<String> addTagData(@RequestBody @Validated(ValidationGroups.Create.class) TagFormDTO tagFormDTO) {
        tagFormDTO.setId(null);
        tagService.addTagData(tagFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public Result<String> delTagData(@PathVariable Integer id) {
        tagService.delTagData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除标签")
    public Result<String> batchDelTagData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        tagService.batchDelTagData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑标签")
    public Result<String> editTagData(@RequestBody @Validated(ValidationGroups.Update.class) TagFormDTO tagFormDTO) {
        tagService.editTagData(tagFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取标签")
    public Result<TagVO> getTagData(@PathVariable Integer id) {
        TagVO data = tagService.getTagData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取标签列表")
    public Result<Map<String, Object>> getTagList(TagFilterDTO tagFilterDTO) {
        Page<TagVO> list = tagService.getTagList(tagFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}/articles")
    @Operation(summary = "获取标签关联的文章列表")
    public Result<Map<String, Object>> getTagArticleList(@PathVariable Integer id, PageDTO pageDTO) {
        Page<ArticleVO> list = tagService.getTagArticleList(id, pageDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }
}