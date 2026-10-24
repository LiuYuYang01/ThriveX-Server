package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.DiaryTokenUtils;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.dto.diary.DiaryFilterDTO;
import liuyuyang.net.dto.diary.DiaryFormDTO;
import liuyuyang.net.dto.diary.DiaryQuestionFormDTO;
import liuyuyang.net.dto.diary.DiaryUnlockDTO;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.diary.DiaryBookVO;
import liuyuyang.net.vo.diary.DiaryQuestionVO;
import liuyuyang.net.vo.diary.DiaryUnlockVO;
import liuyuyang.net.vo.diary.DiaryVO;
import liuyuyang.net.web.service.DiaryQuestionService;
import liuyuyang.net.web.service.DiaryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@Tag(name = "日记本管理")
@RestController
@RequestMapping("/diary")
public class DiaryController {
    /** 访客阅读令牌所在的请求头，由博客服务端代理写入，页面脚本读不到 */
    public static final String DIARY_TOKEN_HEADER = "X-Diary-Token";

    @Resource
    private DiaryService diaryService;
    @Resource
    private DiaryQuestionService diaryQuestionService;
    @Resource
    private DiaryTokenUtils diaryTokenUtils;

    @NoTokenRequired
    @RateLimit
    @GetMapping("/questions")
    @Operation(summary = "获取密语问题（前台，仅问题不含答案）")
    public Result<List<DiaryQuestionVO>> getQuestions() {
        return Result.success(diaryQuestionService.getQuestionListForReader());
    }

    @NoTokenRequired
    @RateLimit(tokens = 8, duration = 60, message = "尝试太频繁了，先喝口水再回来")
    @PostMapping("/unlock")
    @Operation(summary = "用密语解锁日记（前台）")
    public Result<DiaryUnlockVO> unlock(@RequestBody @Validated DiaryUnlockDTO diaryUnlockDTO) {
        DiaryUnlockVO data = diaryQuestionService.verifyAndIssueToken(diaryUnlockDTO.getQuestionId(), diaryUnlockDTO.getAnswer());
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/book")
    @Operation(summary = "按月翻阅日记（前台，需访客令牌）")
    public Result<DiaryBookVO> getDiaryBook(@RequestHeader(value = DIARY_TOKEN_HEADER, required = false) String diaryToken,
                                            @RequestParam(required = false) Integer year,
                                            @RequestParam(required = false) Integer month) {
        if (!diaryTokenUtils.isValid(diaryToken)) {
            throw new CustomException(401, "阅读时间到了，请重新解锁");
        }
        return Result.success(diaryService.getDiaryBook(year, month));
    }

    @GetMapping
    @Operation(summary = "获取日记列表（管理端）")
    public Result<Map<String, Object>> getDiaryList(DiaryFilterDTO diaryFilterDTO) {
        Page<DiaryVO> list = diaryService.getDiaryList(diaryFilterDTO);
        return Result.success(Paging.filter(list));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "获取日记详情（管理端）")
    public Result<DiaryVO> getDiaryData(@PathVariable Integer id) {
        return Result.success(diaryService.getDiaryData(id));
    }

    @PostMapping
    @Operation(summary = "新增日记")
    public Result<String> addDiaryData(@RequestBody @Validated(ValidationGroups.Create.class) DiaryFormDTO diaryFormDTO) {
        diaryFormDTO.setId(null);
        diaryService.addDiaryData(diaryFormDTO);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑日记")
    public Result<String> editDiaryData(@RequestBody @Validated(ValidationGroups.Update.class) DiaryFormDTO diaryFormDTO) {
        diaryService.editDiaryData(diaryFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "删除日记")
    public Result<String> delDiaryData(@PathVariable Integer id) {
        diaryService.delDiaryData(id);
        return Result.success();
    }

    @GetMapping("/credential")
    @Operation(summary = "获取密语列表（管理端）")
    public Result<List<DiaryQuestionVO>> getCredentials() {
        return Result.success(diaryQuestionService.getQuestionListForAdmin());
    }

    @PostMapping("/credential")
    @Operation(summary = "新增密语问答")
    public Result<String> addCredential(@RequestBody @Validated(ValidationGroups.Create.class) DiaryQuestionFormDTO diaryQuestionFormDTO) {
        diaryQuestionFormDTO.setId(null);
        diaryQuestionService.addQuestion(diaryQuestionFormDTO);
        return Result.success();
    }

    @PatchMapping("/credential")
    @Operation(summary = "编辑密语问答（答案留空表示保持不变）")
    public Result<String> editCredential(@RequestBody @Validated(ValidationGroups.Update.class) DiaryQuestionFormDTO diaryQuestionFormDTO) {
        diaryQuestionService.editQuestion(diaryQuestionFormDTO);
        return Result.success();
    }

    @DeleteMapping("/credential/{id}")
    @Operation(summary = "删除密语问答")
    public Result<String> delCredential(@PathVariable Integer id) {
        diaryQuestionService.delQuestion(id);
        return Result.success();
    }
}
