package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.model.User;
import liuyuyang.net.web.service.EnvConfigService;
import liuyuyang.net.web.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "环境配置管理")
@RestController
@RequestMapping("/env_config")
@Validated
public class EnvConfigController {
    @Resource
    private EnvConfigService envConfigService;
    @Resource
    private UserService userService;

    @Operation(summary = "获取环境配置列表")
    @GetMapping("/list")
    public Result<List<EnvConfig>> list() {
        List<EnvConfig> data = envConfigService.list();
        return Result.success("获取成功", data);
    }

    @Operation(summary = "根据ID获取环境配置")
    @GetMapping("/{id}")
    public Result<EnvConfig> getById(
            @Parameter(description = "环境配置ID", required = true, example = "1") @PathVariable Integer id) {
        EnvConfig envConfig = envConfigService.getById(id);
        return envConfig != null ? Result.success("获取成功", envConfig) : Result.error("配置不存在");
    }

    @Operation(summary = "根据名称获取环境配置")
    @GetMapping("/name/{name}")
    public Result<EnvConfig> getByName(
            @Parameter(description = "配置名称", required = true, example = "database_config") @PathVariable String name) {
        EnvConfig envConfig = envConfigService.getByName(name);
        return envConfig != null ? Result.success("获取成功", envConfig) : Result.error("配置不存在");
    }

    @Operation(summary = "根据ID获取配置")
    @PatchMapping("/json/{id}")
    public Result<String> updateJsonValue(
            @Parameter(description = "环境配置ID", required = true, example = "1") @PathVariable Integer id,
            @Parameter(description = "JSON配置值", required = true) @RequestBody @NotEmpty(message = "配置内容不能为空") Map<String, Object> jsonValue) {
        boolean success = envConfigService.updateJsonValue(id, jsonValue);
        return success ? Result.success("JSON配置更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "根据ID更新配置")
    @PatchMapping("/{id}/field/{fieldName}")
    public Result<String> updateJsonFieldValue(
            @Parameter(description = "环境配置ID", required = true, example = "1") @PathVariable Integer id,
            @Parameter(description = "字段名称", required = true, example = "host") @PathVariable @NotBlank(message = "字段名称不能为空") String fieldName,
            @Parameter(description = "字段值", required = true) @RequestBody Object value) {
        boolean success = envConfigService.updateJsonFieldValue(id, fieldName, value);
        return success ? Result.success() : Result.error();
    }

    @NoTokenRequired
    @Operation(summary = "获取公开的配置")
    @GetMapping("/public_config")
    public Result<Map<String, Object>> getPublicConfig() {
        return Result.success(envConfigService.getPublicConfig());
    }

    @NoTokenRequired
    @Operation(summary = "获取系统初始化状态")
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

    @Operation(summary = "更新系统初始化状态")
    @PostMapping("/is_system_init")
    public Result<String> updateSystemInitStatus() {
        EnvConfig envConfig = envConfigService.getByName("is_system_init");
        if (envConfig == null) {
            return Result.error("is_system_init配置不存在");
        }

        // 管理员仍使用默认密码时禁止完成初始化，确保初始化完成即默认凭据已失效
        User admin = userService.getById(1);
        String defaultPasswordMd5 = DigestUtils.md5DigestAsHex("123456".getBytes());
        if (admin != null && defaultPasswordMd5.equals(admin.getPassword())) {
            return Result.error("管理员仍在使用默认密码，请先修改管理员账号密码后再完成初始化");
        }

        envConfigService.updateJsonFieldValue(envConfig.getId(), "value", true);
        return Result.success("系统初始化成功");
    }
}