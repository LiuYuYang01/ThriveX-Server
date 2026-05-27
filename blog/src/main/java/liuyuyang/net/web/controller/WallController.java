package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.RateLimit;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.model.Wall;
import liuyuyang.net.model.WallCate;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.web.service.WallService;
import liuyuyang.net.common.utils.Paging;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.wall.WallFilterVo;
import org.springframework.transaction.annotation.Transactional;
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

    @RateLimit
    @NoTokenRequired
    @PostMapping
    @ApiOperation("新增留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Wall wall) throws Exception {
        wallService.add(wall);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        Wall data = wallService.getById(id);
        if (data == null) return Result.error("删除留言失败：该留言不存在");
        wallService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        wallService.removeByIds(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Wall wall) {
        wallService.updateById(wall);
        return Result.success();
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Wall> get(@PathVariable Integer id) {
        Wall data = wallService.get(id);
        return Result.success(data);
    }

    @RateLimit
    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取留言列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<Wall>> list(@RequestBody WallFilterVo filterVo) {
        List<Wall> list = wallService.list(filterVo);
        return Result.success(list);
    }

    @RateLimit
    @NoTokenRequired
    @PostMapping("/paging")
    @ApiOperation("分页查询留言列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result paging(@RequestBody WallFilterVo filterVo, PageVo pageVo) {
        Page<Wall> list = wallService.paging(filterVo, pageVo);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @RateLimit
    @NoTokenRequired
    @PostMapping("/cate/{cateId}")
    @ApiOperation("获取指定分类中所有留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result getCateWallList(@PathVariable Integer cateId, PageVo pageVo) {
        Page<Wall> list = wallService.getCateWallList(cateId, pageVo);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/cate")
    @ApiOperation("获取留言分类列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    public Result getCateList() {
        List<WallCate> list = wallService.getCateList();
        return Result.success(list);
    }

    @PatchMapping("/audit/{id}")
    @ApiOperation("审核指定留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 10)
    public Result auditWall(@PathVariable Integer id) {
        Wall data = wallService.getById(id);

        if (data == null) throw new CustomException(400, "该留言不存在");

        data.setAuditStatus(1);
        wallService.updateById(data);
        return Result.success();
    }

    @PatchMapping("/choice/{id}")
    @ApiOperation("设置与取消精选留言")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 11)
    public Result updateChoice(@PathVariable Integer id) {
        wallService.updateChoice(id);
        return Result.success();
    }
}
