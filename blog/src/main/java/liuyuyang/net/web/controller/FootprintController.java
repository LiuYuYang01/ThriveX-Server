package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.model.Footprint;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.FootprintService;
import liuyuyang.net.vo.FilterVo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "足迹管理")
@RestController
@RequestMapping("/footprint")
@Transactional
public class FootprintController {
    @Resource
    private FootprintService footprintService;

    @PostMapping
    @ApiOperation("新增足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Footprint footprint) {
        footprintService.save(footprint);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        footprintService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        footprintService.removeByIds(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Footprint footprint) {
        footprintService.updateById(footprint);
        return Result.success();
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Footprint> get(@PathVariable Integer id) {
        Footprint data = footprintService.getById(id);
        return Result.success(data);
    }

    @RateLimit
    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取足迹列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<Footprint>> list(@RequestBody FilterVo filterVo) {
        List<Footprint> data = footprintService.list(filterVo);
        return Result.success(data);
    }
}
