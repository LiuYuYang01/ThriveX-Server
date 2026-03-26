package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.RateLimit;
import liuyuyang.net.model.Tag;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.web.service.TagService;
import liuyuyang.net.common.utils.Paging;
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
    public Result<String> addTagData(@RequestBody Tag tag) {
        tag.setId(null);
        boolean res = tagService.addTagData(tag);
        return res ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delTagData(@PathVariable Integer id) {
        Tag data = tagService.getById(id);
        if (data == null) return Result.error("该数据不存在");

        boolean res = tagService.removeById(id);
        return res ? Result.success() : Result.error();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDel(@RequestBody List<Integer> ids) {
        boolean res = tagService.removeByIds(ids);
        return res ? Result.success() : Result.error();
    }

    @PatchMapping
    @ApiOperation("编辑标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editTagData(@RequestBody Tag tag) {
        boolean res = tagService.updateById(tag);
        return res ? Result.success() : Result.error();
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取标签")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Tag> getTagData(@PathVariable Integer id) {
        Tag data = tagService.getById(id);
        if (data == null) return Result.error("该数据不存在");
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取标签列表", notes = "不传 page/size 返回全部，传则分页")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<Map<String, Object>> getTagPaging(PageVo pageVo) {
        Page<Tag> data = tagService.getTagList(pageVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}