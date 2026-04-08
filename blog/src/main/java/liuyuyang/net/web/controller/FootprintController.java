package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.footprint.FootprintFilterDTO;
import liuyuyang.net.dto.footprint.FootprintFormDTO;
import liuyuyang.net.vo.footprint.FootprintVO;
import liuyuyang.net.web.service.FootprintService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    public Result<String> addFootprintData(@RequestBody FootprintFormDTO footprintFormDTO) {
        footprintFormDTO.setId(null);
        footprintService.addFootprintData(footprintFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delFootprintData(@PathVariable Integer id) {
        footprintService.delFootprintData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelFootprintData(@RequestBody List<Integer> ids) {
        footprintService.batchDelFootprintData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editFootprintData(@RequestBody FootprintFormDTO footprintFormDTO) {
        footprintService.editFootprintData(footprintFormDTO);
        return Result.success();
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取足迹")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<FootprintVO> getFootprintData(@PathVariable Integer id) {
        FootprintVO data = footprintService.getFootprintData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取足迹列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getFootprintList(FootprintFilterDTO filterVo) {
        Page<FootprintVO> data = footprintService.getFootprintList(filterVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
