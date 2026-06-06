package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "环境配置管理")
@RestController
@RequestMapping("/env_config")
@Validated
public class EnvConfigController {
    @Resource
    private EnvConfigService envConfigService;

    @ApiOperation("获取环境配置列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    @GetMapping("/list")
    public Result<List<EnvConfig>> list() {
        List<EnvConfig> data = envConfigService.list();
        return Result.success("获取成功", data);
    }

    @ApiOperation("根据ID获取环境配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    @GetMapping("/{id}")
    public Result<EnvConfig> getById(
            @ApiParam(value = "环境配置ID", required = true, example = "1") @PathVariable Integer id) {
        EnvConfig envConfig = envConfigService.getById(id);
        return envConfig != null ? Result.success("获取成功", envConfig) : Result.error("配置不存在");
    }

    @ApiOperation("根据名称获取环境配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    @GetMapping("/name/{name}")
    public Result<EnvConfig> getByName(
            @ApiParam(value = "配置名称", required = true, example = "database_config") @PathVariable String name) {
        EnvConfig envConfig = envConfigService.getByName(name);
        return envConfig != null ? Result.success("获取成功", envConfig) : Result.error("配置不存在");
    }

    @ApiOperation("根据ID获取配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    @PatchMapping("/json/{id}")
    public Result<String> updateJsonValue(
            @ApiParam(value = "环境配置ID", required = true, example = "1") @PathVariable Integer id,
            @ApiParam(value = "JSON配置值", required = true) @RequestBody @NotEmpty(message = "配置内容不能为空") Map<String, Object> jsonValue) {
        boolean success = envConfigService.updateJsonValue(id, jsonValue);
        return success ? Result.success("JSON配置更新成功") : Result.error("更新失败");
    }

    @ApiOperation("根据ID更新配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    @PatchMapping("/{id}/field/{fieldName}")
    public Result<String> updateJsonFieldValue(
            @ApiParam(value = "环境配置ID", required = true, example = "1") @PathVariable Integer id,
            @ApiParam(value = "字段名称", required = true, example = "host") @PathVariable @NotBlank(message = "字段名称不能为空") String fieldName,
            @ApiParam(value = "字段值", required = true) @RequestBody Object value) {
        boolean success = envConfigService.updateJsonFieldValue(id, fieldName, value);
        return success ? Result.success() : Result.error();
    }

    @NoTokenRequired
    @ApiOperation("获取高德地图配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    @GetMapping("/gaode_map")
    public Result<Map<String, Object>> getGaodeMapConfig() {
        EnvConfig envConfig = envConfigService.getByName("gaode_map");
        return Result.success(envConfig.getValue());
    }

    @NoTokenRequired
    @ApiOperation("获取公开的配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    @GetMapping("/public_config")
    public Result<Map<String, Object>> getPublicConfig() {
        return Result.success(envConfigService.getPublicConfig());
    }

    @NoTokenRequired
    @ApiOperation("获取系统初始化状态")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    @GetMapping("/is_system_init")
    public Result<Map<String, Object>> getSystemInitStatus() {
        EnvConfig envConfig = envConfigService.getByName("is_system_init");
        boolean isSystemInit = false;
        if (envConfig != null && envConfig.getValue() != null) {
            Object value = envConfig.getValue().get("value");
            if (value instanceof Boolean) {
                isSystemInit = (Boolean) value;
            } else if (value instanceof String) {
                isSystemInit = Boolean.parseBoolean((String) value);
            }
        }
        Map<String, Object> data = new HashMap<>(1);
        data.put("is_system_init", isSystemInit);
        return Result.success(data);
    }

    @NoTokenRequired
    @ApiOperation("更新系统初始化状态")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 10)
    @PostMapping("/is_system_init")
    public Result<String> updateSystemInitStatus() {
        EnvConfig envConfig = envConfigService.getByName("is_system_init");
        if (envConfig == null) {
            return Result.error("is_system_init配置不存在");
        }
        envConfigService.updateJsonFieldValue(envConfig.getId(), "value", true);
        return Result.success("系统初始化成功");
    }
}