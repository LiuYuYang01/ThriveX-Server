package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Comment;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.vo.comment.CommentFilterVo;

import java.util.List;

public interface CommentService extends IService<Comment> {
    void add(Comment comment) throws Exception;

    Comment get(Integer id);

    Page<Comment> getArticleCommentList(Integer articleId, PageDTO pageDto);

    List<Comment> list(CommentFilterVo filterVo);

    Page<Comment> paging(CommentFilterVo filterVo, PageDTO pageDto);
}
