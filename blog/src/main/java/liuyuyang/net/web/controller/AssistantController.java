package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.assistant.AssistantFilterDTO;
import liuyuyang.net.dto.assistant.AssistantFormDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.assistant.AssistantVO;
import liuyuyang.net.web.service.AssistantService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Api(tags = "助手管理")
@RestController
@RequestMapping("/assistant")
@Transactional
@Validated
public class AssistantController {
    @Resource
    private AssistantService assistantService;

    @PostMapping
    @ApiOperation("新增助手")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addAssistantData(@RequestBody @Validated(ValidationGroups.Create.class) AssistantFormDTO assistantFormDTO) {
        assistantFormDTO.setId(null);
        assistantService.addAssistantData(assistantFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除助手")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delAssistantData(@PathVariable Integer id) {
        assistantService.delAssistantData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除助手")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelAssistantData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        assistantService.batchDelAssistantData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑助手")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editAssistantData(@RequestBody @Validated(ValidationGroups.Update.class) AssistantFormDTO assistantFormDTO) {
        assistantService.editAssistantData(assistantFormDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取助手")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<AssistantVO> getAssistantData(@PathVariable Integer id) {
        AssistantVO data = assistantService.getAssistantData(id);
        return Result.success(data);
    }

    @GetMapping
    @ApiOperation("获取助手列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getAssistantList(AssistantFilterDTO assistantFilterDTO) {
        Page<AssistantVO> list = assistantService.getAssistantList(assistantFilterDTO);
        Map<String, Object> result = Paging.filter(list);
        return Result.success(result);
    }

    @PatchMapping("/default/{id}")
    @ApiOperation("设置默认助手")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<String> selectDefaultAssistant(@PathVariable Integer id) {
        // 将之前的都设置为 0 表示未选中
        assistantService.selectDefaultAssistant(id);
        // 将当前的设置为 1 选中状态
        return Result.success();
    }
}