package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.swiper.SwiperFilterDTO;
import liuyuyang.net.dto.swiper.SwiperFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.swiper.SwiperVO;
import liuyuyang.net.web.service.SwiperService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Api(tags = "轮播图管理")
@RestController
@RequestMapping("/swiper")
@Transactional
@Validated
public class SwiperController {
    @Resource
    private SwiperService swiperService;

    @PostMapping
    @ApiOperation("新增轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addSwiperData(@RequestBody @Validated(ValidationGroups.Create.class) SwiperFormDTO swiperFormDTO) {
        swiperFormDTO.setId(null);
        swiperService.addSwiperData(swiperFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delSwiperData(@PathVariable Integer id) {
        swiperService.delSwiperData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelSwiperData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        swiperService.batchDelSwiperData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editSwiperData(@RequestBody @Validated(ValidationGroups.Update.class) SwiperFormDTO swiperFormDTO) {
        swiperService.editSwiperData(swiperFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取轮播图列表", notes = "不传 page/size 返回全部，传则分页")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Map<String, Object>> getSwiperList(SwiperFilterDTO swiperFilterDTO) {
        Page<SwiperVO> list = swiperService.getSwiperList(swiperFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<SwiperVO> getSwiperData(@PathVariable Integer id) {
        SwiperVO data = swiperService.getSwiperData(id);
        return Result.success(data);
    }

    @PatchMapping("/sort")
    @ApiOperation("轮播图拖拽排序")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<String> sortSwiperData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        swiperService.sortSwiperData(ids);
        return Result.success();
    }
}
