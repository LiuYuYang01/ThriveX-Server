package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.model.LinkType;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.CaptchaService;
import liuyuyang.net.web.service.LinkService;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.dto.link.LinkFilterDTO;
import liuyuyang.net.dto.link.LinkFormDTO;
import liuyuyang.net.dto.link.LinkSortDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.link.LinkVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "网站管理")
@RestController
@RequestMapping("/link")
@Transactional
@Validated
public class LinkController {
    @Resource
    private LinkService linkService;
    @Resource
    private CaptchaService captchaService;

    @RateLimit
    @PostMapping
    @NoTokenRequired
    @Operation(summary = "新增网站")
    public Result<String> addLinkData(@RequestBody @Validated(ValidationGroups.Create.class) LinkFormDTO linkFormDTO, @RequestHeader(value = "Authorization", required = false) String token) throws Exception {
        captchaService.check(linkFormDTO.getCaptchaToken());
        linkFormDTO.setId(null);
        linkService.addLinkData(linkFormDTO, token);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除网站")
    public Result<String> delLinkData(@PathVariable Integer id) {
        linkService.delLinkData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除网站")
    public Result<String> batchDelLinkData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        linkService.batchDelLinkData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑网站")
    public Result<String> editLinkData(@RequestBody @Validated(ValidationGroups.Update.class) LinkFormDTO linkFormDTO) {
        linkService.editLinkData(linkFormDTO);
        return Result.success();
    }

    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取网站")
    public Result<LinkVO> getLinkData(@PathVariable Integer id) {
        LinkVO data = linkService.getLinkData(id);
        return Result.success(data);
    }

    @RateLimit
    @NoTokenRequired
    @GetMapping
    @Operation(summary = "获取网站列表", description = "不传 page/size 返回全部，传则分页（来自 filterVo）")
    public Result<Map<String, Object>> getLinkList(LinkFilterDTO filterVo) {
        Page<LinkVO> data = linkService.getLinkList(filterVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/type")
    @Operation(summary = "获取网站类型列表")
    public Result<List<LinkType>> getLinkTypeList() {
        List<LinkType> data = linkService.getLinkTypeList();
        return Result.success(data);
    }

    @PatchMapping("/audit/{id}")
    @Operation(summary = "审核指定网站")
    public Result<String> auditLinkData(@PathVariable Integer id) {
        linkService.auditLinkData(id);
        return Result.success();
    }

    @PatchMapping("/sort")
    @Operation(summary = "网站拖拽排序（同类型内）")
    public Result<String> sortLinkData(@RequestBody @Valid LinkSortDTO linkSortDTO) {
        linkService.sortLinkData(linkSortDTO);
        return Result.success();
    }
}
