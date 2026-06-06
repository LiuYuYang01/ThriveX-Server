package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Api(tags = "评论管理")
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
    @ApiOperation("新增评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> addCommentData(@RequestBody @Validated(ValidationGroups.Create.class) CommentFormDTO commentFormDTO) throws Exception {
        commentFormDTO.setId(null);
        commentService.addCommentData(commentFormDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> delCommentData(@PathVariable Integer id) {
        commentService.delCommentData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDelCommentData(@RequestBody @NotEmpty(message = "ID列表不能为空") List<Integer> ids) {
        commentService.batchDelCommentData(ids);
        return Result.success();
    }

    @PatchMapping
    @ApiOperation("编辑评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> editCommentData(@RequestBody @Validated(ValidationGroups.Update.class) CommentFormDTO commentFormDTO) {
        commentService.editCommentData(commentFormDTO);
        return Result.success();
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/{id}")
    @ApiOperation("获取评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<CommentVO> getCommentData(@PathVariable Integer id) {
        CommentVO data = commentService.getCommentData(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping
    @ApiOperation(value = "获取评论列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<Map<String, Object>> getCommentList(CommentFilterDTO linkFilterDTO) {
        return Result.success(Paging.filter(commentService.getCommentList(linkFilterDTO)));
    }

    @NoTokenRequired
    @RateLimit
    @GetMapping("/article/{articleId}")
    @ApiOperation("获取指定文章中所有评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result<Map<String, Object>> getArticleCommentList(@PathVariable Integer articleId, PageDTO pageDTO) {
        return Result.success(Paging.filter(commentService.getArticleCommentList(articleId, pageDTO)));
    }

    @PatchMapping("/audit/{id}")
    @ApiOperation("审核指定评论")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<String> auditCommentData(@PathVariable Integer id) {
        commentService.auditCommentData(id);
        return Result.success();
    }
}
