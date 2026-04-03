package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.comment.CommentFilterDTO;
import liuyuyang.net.dto.comment.CommentFormDTO;
import liuyuyang.net.model.Comment;
import liuyuyang.net.vo.comment.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    void addCommentData(CommentFormDTO commentFormDTO) throws Exception;

    void delCommentData(Integer id);

    void batchDelCommentData(List<Integer> ids);

    void editCommentData(CommentFormDTO commentFormDTO);

    CommentVO getCommentData(Integer id);

    Page<CommentVO> getCommentList(CommentFilterDTO filterVo);

    Page<CommentVO> getArticleCommentList(Integer articleId, PageDTO pageDTO);

    void auditCommentData(Integer id);
}
