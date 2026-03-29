package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Comment;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.vo.comment.CommentFilterDTO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    void add(Comment comment) throws Exception;

    Comment get(Integer id);

    Page<Comment> getArticleCommentList(Integer articleId, PageDTO pageDTO);

    List<Comment> list(CommentFilterDTO filterVo);

    Page<Comment> paging(CommentFilterDTO filterVo, PageDTO pageDTO);
}
