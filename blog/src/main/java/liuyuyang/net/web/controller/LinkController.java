package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.Paging;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.LinkType;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.link.LinkFilterVo;
import liuyuyang.net.web.mapper.LinkTypeMapper;
import liuyuyang.net.web.service.LinkService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "网站管理")
@RestController
@RequestMapping("/link")
@Transactional
public class LinkController {
    @Resource
    private LinkService linkService;
    @Resource
    private LinkTypeMapper linkTypeMapper;

    @PostMapping
    @NoTokenRequired
    @ApiOperation("新增网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Link link, @RequestHeader(value = "Authorization", required = false) String token) throws Exception {
        linkService.add(link, token);
        return Result.success();
    }

    @PremName("link:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        Link data = linkService.getById(id);
        if (data == null) return Result.error("该数据不存在");
        linkService.removeById(id);
        return Result.success();
    }

    @PremName("link:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result delBatch(@RequestBody List<Integer> ids) {
        linkService.removeByIds(ids);
        return Result.success();
    }

    @PremName("link:edit")
    @PatchMapping
    @ApiOperation("编辑网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Link link) {
        linkService.updateById(link);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Link> get(@PathVariable Integer id) {
        Link data = linkService.getById(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取网站列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<Link>> list(@RequestBody LinkFilterVo filterVo) {
        List<Link> data = linkService.list(filterVo);
        return Result.success(data);
    }

    @NoTokenRequired
    @PostMapping("/paging")
    @ApiOperation("分页查询网站列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result paging(@RequestBody LinkFilterVo filterVo, PageVo pageVo) {
        Page<Link> data = linkService.paging(filterVo, pageVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @GetMapping("/type")
    @ApiOperation("获取网站类型列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<List<LinkType>> typeList() {
        List<LinkType> data = linkTypeMapper.selectList(null);
        return Result.success(data);
    }

    @PremName("link:audit")
    @PatchMapping("/audit/{id}")
    @ApiOperation("审核指定网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    public Result<?> auditWeb(@PathVariable Integer id) {
        Link data = linkService.getById(id);

        if (data == null) throw new CustomException(400, "该网站不存在");

        data.setAuditStatus(1);
        linkService.updateById(data);
        return Result.success();
    }

    /**
     * 获取网站信息
     *
     * @param url 网址
     * @return {@link Result }<{@link Link }>
     */
    @GetMapping("/website-info")
    public Result<Link> getWebsiteInfo(@RequestParam String url) throws Exception {
        Link info = linkService.fetchWebsiteInfo(url);
        return Result.success(info);
    }
}
