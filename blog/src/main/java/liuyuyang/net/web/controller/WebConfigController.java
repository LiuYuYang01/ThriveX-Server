package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.WebConfigService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import liuyuyang.net.model.WebConfig;

@Tag(name = "网站配置管理")
@RestController
@RequestMapping("/web_config")
@Transactional
@Validated
public class WebConfigController {
    @Resource
    private WebConfigService webConfigService;

    @RateLimit
    @Operation(summary = "获取网站配置列表")
    @GetMapping("/list")
    public Result<List<WebConfig>> list() {
        List<WebConfig> data = webConfigService.list();
        return Result.success("获取成功", data);
    }

    @NoTokenRequired
    @RateLimit
    @Operation(summary = "根据名称获取网站配置")
    @GetMapping("/name/{name}")
    public Result<WebConfig> getByName(@PathVariable String name) {
        WebConfig webConfig = webConfigService.getByName(name);
        return webConfig != null ? Result.success("获取成功", webConfig) : Result.error("配置不存在");
    }

    @NoTokenRequired
    @RateLimit
    @Operation(summary = "根据ID获取网站配置")
    @GetMapping("/{id}")
    public Result<WebConfig> getById(@PathVariable Integer id) {
        WebConfig webConfig = webConfigService.getById(id);
        return webConfig != null ? Result.success("获取成功", webConfig) : Result.error("配置不存在");
    }

    @RateLimit
    @Operation(summary = "根据ID更新网站配置")
    @PatchMapping("/json/{id}")
    public Result<String> updateJsonValue(@PathVariable Integer id, @RequestBody @NotEmpty(message = "配置内容不能为空") Map<String, Object> jsonValue) {
        boolean success = webConfigService.updateJsonValue(id, jsonValue);
        return success ? Result.success() : Result.error();
    }

    @RateLimit
    @Operation(summary = "根据名称更新网站配置")
    @PatchMapping("/json/name/{name}")
    public Result<String> updateJsonValueByName(@PathVariable String name, @RequestBody @NotEmpty(message = "配置内容不能为空") Map<String, Object> jsonValue) {
        boolean success = webConfigService.updateJsonValueByName(name, jsonValue);
        return success ? Result.success() : Result.error();
    }
}