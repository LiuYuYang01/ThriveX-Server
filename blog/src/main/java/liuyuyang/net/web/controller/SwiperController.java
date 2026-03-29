package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.service.SwiperService;
import liuyuyang.net.core.utils.Paging;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "轮播图管理")
@RestController
@RequestMapping("/swiper")
@Transactional
public class SwiperController {
    @Resource
    private SwiperService swiperService;

    @PostMapping
    @ApiOperation("新增轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addSwiperData(@RequestBody Swiper swiper) {
        swiper.setId(null);
        boolean res = swiperService.save(swiper);
        return res ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delSwiperData(@PathVariable Integer id) {
        Swiper data = swiperService.getById(id);
        if (data == null) return Result.error("该数据不存在");

        boolean res = swiperService.removeById(id);
        return res ? Result.success() : Result.error();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDel(@RequestBody List<Integer> ids) {
        boolean res = swiperService.removeByIds(ids);
        return res ? Result.success() : Result.error();
    }

    @PatchMapping
    @ApiOperation("编辑轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editSwiperData(@RequestBody Swiper swiper) {
        boolean res = swiperService.updateById(swiper);
        return res ? Result.success() : Result.error();
    }

    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Swiper> getSwiperData(@PathVariable Integer id) {
        Swiper data = swiperService.getById(id);
        if(data == null) Result.error("该数据不存在");
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取轮播图列表", notes = "不传 page/size 返回全部，传则分页")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getSwiperList(PageDTO pageDTO) {
        Page<Swiper> data = swiperService.getSwiperList(pageDTO);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
