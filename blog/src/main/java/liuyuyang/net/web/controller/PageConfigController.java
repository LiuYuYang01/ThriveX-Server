package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.model.PageConfig;
import liuyuyang.net.web.service.PageConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "页面配置管理")
@RestController
@RequestMapping("/page_config")
@Validated
public class PageConfigController {
    @Resource
    private PageConfigService pageConfigService;

    @RateLimit
    @Operation(summary = "获取页面配置列表")
    @GetMapping("/list")
    public Result<List<PageConfig>> list() {
        List<PageConfig> data = pageConfigService.list();
        return Result.success("获取成功", data);
    }

    @NoTokenRequired
    @RateLimit
    @Operation(summary = "根据名称获取页面配置")
    @GetMapping("/name/{name}")
    public Result<PageConfig> getByName(@Parameter(description = "配置名称", required = true, example = "home_page") @PathVariable String name) {
        PageConfig pageConfig = pageConfigService.getByName(name);
        return pageConfig != null ? Result.success("获取成功", pageConfig) : Result.error("配置不存在");
    }

    @RateLimit
    @Operation(summary = "根据ID获取页面配置")
    @GetMapping("/{id}")
    public Result<PageConfig> getById(@Parameter(description = "页面配置ID", required = true, example = "1") @PathVariable Integer id) {
        PageConfig pageConfig = pageConfigService.getById(id);
        return pageConfig != null ? Result.success("获取成功", pageConfig) : Result.error("配置不存在");
    }

    @RateLimit
    @Operation(summary = "根据ID更新页面配置")
    @PatchMapping("/json/{id}")
    public Result<String> updateJsonValue(
            @Parameter(description = "页面配置ID", required = true, example = "1") @PathVariable Integer id,
            @Parameter(description = "JSON配置值", required = true) @RequestBody @NotEmpty(message = "配置内容不能为空") Map<String, Object> jsonValue) {
        boolean success = pageConfigService.updateJsonValue(id, jsonValue);
        return success ? Result.success() : Result.error();
    }
} 