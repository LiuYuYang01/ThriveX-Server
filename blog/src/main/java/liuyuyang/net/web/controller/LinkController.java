package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.model.LinkType;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.LinkService;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.dto.link.LinkFilterDTO;
import liuyuyang.net.dto.link.LinkFormDTO;
import liuyuyang.net.vo.link.LinkVO;
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

    @RateLimit
    @PostMapping
    @NoTokenRequired
    @ApiOperation("新增网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addLinkData(@RequestBody LinkFormDTO linkFormDTO, @RequestHeader(value = "Authorization", required = false) String token) throws Exception {
        linkFormDTO.setId(null);
        linkService.addLinkData(linkFormDTO, token);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delLinkData(@PathVariable Integer id) {
        linkService.delLinkData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelLinkData(@RequestBody List<Integer> ids) {
        linkService.batchDelLinkData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editLinkData(@RequestBody LinkFormDTO linkFormDTO) {
        linkService.editLinkData(linkFormDTO);
        return Result.success();
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<LinkVO> getLinkData(@PathVariable Integer id) {
        LinkVO data = linkService.getLinkData(id);
        return Result.success(data);
    }

    @RateLimit
    @NoTokenRequired
    @GetMapping
    @ApiOperation(value = "获取网站列表", notes = "不传 page/size 返回全部，传则分页（来自 filterVo）")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getLinkList(LinkFilterDTO filterVo) {
        Page<LinkVO> data = linkService.getLinkList(filterVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/type")
    @ApiOperation("获取网站类型列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<List<LinkType>> getLinkTypeList() {
        List<LinkType> data = linkService.getLinkTypeList();
        return Result.success(data);
    }

    @PatchMapping("/audit/{id}")
    @ApiOperation("审核指定网站")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<String> auditLinkData(@PathVariable Integer id) {
        linkService.auditLinkData(id);
        return Result.success();
    }
}
