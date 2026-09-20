package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
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

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/swiper")
@Transactional
@Validated
public class SwiperController {
    @Resource
    private SwiperService swiperService;

    @PostMapping
    @Operation(summary = "新增轮播图")
    public Result<String> addSwiperData(@RequestBody @Validated(ValidationGroups.Create.class) SwiperFormDTO swiperFormDTO) {
        swiperFormDTO.setId(null);
        swiperService.addSwiperData(swiperFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除轮播图")
    public Result<String> delSwiperData(@PathVariable Integer id) {
        swiperService.delSwiperData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除轮播图")
    public Result<String> batchDelSwiperData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        swiperService.batchDelSwiperData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑轮播图")
    public Result<String> editSwiperData(@RequestBody @Validated(ValidationGroups.Update.class) SwiperFormDTO swiperFormDTO) {
        swiperService.editSwiperData(swiperFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取轮播图列表", description = "不传 page/size 返回全部，传则分页")
    public Result<Map<String, Object>> getSwiperList(SwiperFilterDTO swiperFilterDTO) {
        Page<SwiperVO> list = swiperService.getSwiperList(swiperFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取轮播图")
    public Result<SwiperVO> getSwiperData(@PathVariable Integer id) {
        SwiperVO data = swiperService.getSwiperData(id);
        return Result.success(data);
    }

    @PatchMapping("/sort")
    @Operation(summary = "轮播图拖拽排序")
    public Result<String> sortSwiperData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        swiperService.sortSwiperData(ids);
        return Result.success();
    }
}
