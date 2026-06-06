package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import liuyuyang.net.web.service.WallService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "留言管理")
@RestController
@RequestMapping("/wall")
@Transactional
public class WallController {
    @Resource
    private WallService wallService;

    @NoTokenRequired
    @RateLimit
    @PostMapping
    @ApiOperation("新增留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addWallData(@RequestBody @Validated(ValidationGroups.Create.class) WallFormDTO wallFormDTO) throws Exception {
        wallFormDTO.setId(null);
        wallService.addWallData(wallFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delWallData(@PathVariable Integer id) {
        wallService.delWallData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelWallData(@RequestBody List<Integer> ids) {
        wallService.batchDelWallData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editWallData(@RequestBody WallFormDTO wallFormDTO) {
        wallService.editWallData(wallFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取留言列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Map<String, Object>> getWallList(WallFilterDTO wallFilterDTO) {
        Page<WallVO> list = wallService.getWallList(wallFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/cate")
    @ApiOperation("获取留言分类列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<WallCate>> getWallCateList() {
        List<WallCate> list = wallService.getWallCateList();
        return Result.success(list);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/cate/{cate_id}")
    @ApiOperation("获取指定分类中所有留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<Map<String, Object>> getCateWallList(@PathVariable Integer cate_id, PageDTO pageDTO) {
        Page<WallVO> list = wallService.getCateWallList(cate_id, pageDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<WallVO> getWallData(@PathVariable Integer id) {
        WallVO data = wallService.getWallData(id);
        return Result.success(data);
    }

    @PatchMapping("/audit/{id}")
    @ApiOperation("审核指定留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    public Result<String> auditWallData(@PathVariable Integer id) {
        wallService.auditWallData(id);
        return Result.success();
    }

    @PatchMapping("/choice/{id}")
    @ApiOperation("设置与取消精选留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 10)
    public Result<String> updateWallChoice(@PathVariable Integer id) {
        wallService.updateWallChoice(id);
        return Result.success();
    }
}
