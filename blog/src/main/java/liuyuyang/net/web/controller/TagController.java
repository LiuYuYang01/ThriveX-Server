package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.tag.TagFilterDTO;
import liuyuyang.net.dto.tag.TagFormDTO;
import liuyuyang.net.vo.tag.TagVO;
import liuyuyang.net.web.service.TagService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "标签管理")
@RestController
@RequestMapping("/tag")
@Transactional
public class TagController {
    @Resource
    private TagService tagService;

    @PostMapping
    @ApiOperation("新增标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addTagData(@RequestBody TagFormDTO tagFormDTO) {
        tagFormDTO.setId(null);
        tagService.addTagData(tagFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delTagData(@PathVariable Integer id) {
        tagService.delTagData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelTagData(@RequestBody List<Integer> ids) {
        tagService.batchDelTagData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editTagData(@RequestBody TagFormDTO tagFormDTO) {
        tagService.editTagData(tagFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<TagVO> getTagData(@PathVariable Integer id) {
        TagVO data = tagService.getTagData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取标签列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getTagList(TagFilterDTO filterVo) {
        Page<TagVO> list = tagService.getTagList(filterVo);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }
}