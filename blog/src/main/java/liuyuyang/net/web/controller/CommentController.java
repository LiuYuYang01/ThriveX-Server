package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import liuyuyang.net.core.annotation.NoTokenRequired;
import liuyuyang.net.core.annotation.RateLimit;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.comment.CommentFilterDTO;
import liuyuyang.net.dto.comment.CommentFormDTO;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.core.utils.Paging;
import liuyuyang.net.validation.ValidationGroups;
import liuyuyang.net.vo.comment.CommentVO;
import liuyuyang.net.web.service.CommentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Tag(name = "评论管理")
@RestController
@RequestMapping("/comment")
@Transactional
@Validated
public class CommentController {
    @Resource
    private CommentService commentService;

    @NoTokenRequired
    @RateLimit
    @PostMapping
    @Operation(summary = "新增评论")
    public Result<String> addCommentData(@RequestBody @Validated(ValidationGroups.Create.class) CommentFormDTO commentFormDTO) throws Exception {
        commentFormDTO.setId(null);
        commentService.addCommentData(commentFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public Result<String> delCommentData(@PathVariable Integer id) {
        commentService.delCommentData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除评论")
    public Result<String> batchDelCommentData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        commentService.batchDelCommentData(ids);
        return Result.success();
    }

    @PatchMapping
    @Operation(summary = "编辑评论")
    public Result<String> editCommentData(@RequestBody @Validated(ValidationGroups.Update.class) CommentFormDTO commentFormDTO) {
        commentService.editCommentData(commentFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @Operation(summary = "获取评论")
    public Result<CommentVO> getCommentData(@PathVariable Integer id) {
        CommentVO data = commentService.getCommentData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @Operation(summary = "获取评论列表")
    public Result<Map<String, Object>> getCommentList(CommentFilterDTO linkFilterDTO) {
        return Result.success(Paging.filter(commentService.getCommentList(linkFilterDTO)));
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/article/{articleId}")
    @Operation(summary = "获取指定文章中所有评论")
    public Result<Map<String, Object>> getArticleCommentList(@PathVariable Integer articleId, PageDTO pageDTO) {
        return Result.success(Paging.filter(commentService.getArticleCommentList(articleId, pageDTO)));
    }

    @PatchMapping("/audit/{id}")
    @Operation(summary = "审核指定评论")
    public Result<String> auditCommentData(@PathVariable Integer id) {
        commentService.auditCommentData(id);
        return Result.success();
    }
}
