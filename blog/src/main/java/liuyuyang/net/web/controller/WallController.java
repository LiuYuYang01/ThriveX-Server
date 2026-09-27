package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.wall.WallFilterDTO;
import liuyuyang.net.dto.wall.WallFormDTO;
import liuyuyang.net.model.WallCate;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.wall.WallVO;
import liuyuyang.net.web.service.CaptchaService;
import liuyuyang.net.web.service.WallService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "留言管理")
@RestController
@RequestMapping("/wall")
@Transactional
@Validated
public class WallController {
    @Resource
    private WallService wallService;
    @Resource
    private CaptchaService captchaService;

    @NoTokenRequired
    @RateLimit
    @PostMapping
    @Operation(summary = "新增留言")
    public Result<String> addWallData(@RequestBody @Validated(ValidationGroups.Create.class) WallFormDTO wallFormDTO) throws Exception {
        captchaService.check(wallFormDTO.getCaptchaToken());
        wallFormDTO.setId(null);
        wallService.addWallData(wallFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除留言")
    public Result<String> delWallData(@PathVariable Integer id) {
        wallService.delWallData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除留言")
    public Result<String> batchDelWallData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        wallService.batchDelWallData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑留言")
    public Result<String> editWallData(@RequestBody @Validated(ValidationGroups.Update.class) WallFormDTO wallFormDTO) {
        wallService.editWallData(wallFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取留言列表")
    public Result<Map<String, Object>> getWallList(WallFilterDTO wallFilterDTO) {
        Page<WallVO> list = wallService.getWallList(wallFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/cate")
    @Operation(summary = "获取留言分类列表")
    public Result<List<WallCate>> getWallCateList() {
        List<WallCate> list = wallService.getWallCateList();
        return Result.success(list);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/cate/{cate_id}")
    @Operation(summary = "获取指定分类中所有留言")
    public Result<Map<String, Object>> getCateWallList(@PathVariable Integer cate_id, PageDTO pageDTO) {
        Page<WallVO> list = wallService.getCateWallList(cate_id, pageDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取留言")
    public Result<WallVO> getWallData(@PathVariable Integer id) {
        WallVO data = wallService.getWallData(id);
        return Result.success(data);
    }

    @PatchMapping("/audit/{id}")
    @Operation(summary = "审核指定留言")
    public Result<String> auditWallData(@PathVariable Integer id) {
        wallService.auditWallData(id);
        return Result.success();
    }

    @PatchMapping("/choice/{id}")
    @Operation(summary = "设置与取消精选留言")
    public Result<String> updateWallChoice(@PathVariable Integer id) {
        wallService.updateWallChoice(id);
        return Result.success();
    }
}
