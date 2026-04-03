package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.comment.CommentFilterDTO;
import liuyuyang.net.dto.comment.CommentFormDTO;
import liuyuyang.net.model.Article;
import liuyuyang.net.model.Comment;
import liuyuyang.net.vo.comment.CommentVO;
import liuyuyang.net.web.mapper.ArticleMapper;
import liuyuyang.net.web.mapper.CommentMapper;
import liuyuyang.net.web.service.CommentService;
import liuyuyang.net.web.service.WebConfigService;
import liuyuyang.net.core.utils.EmailUtils;
import liuyuyang.net.core.utils.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private EmailUtils emailUtils;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private WebConfigService configService;

    @Override
    public void addCommentData(CommentFormDTO commentFormDTO) throws Exception {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentFormDTO, comment);
        commentMapper.insert(comment);

        // 文章标题
        String title = articleMapper.selectById(comment.getArticleId()).getTitle();

        // 评论记录
        StringBuilder content = new StringBuilder();
        // 判断是否还有上一条评论
        Comment prev_comment = null;
        if (comment.getCommentId() != 0) {
            prev_comment = commentMapper.selectById(comment.getCommentId());
            content.append(prev_comment.getName()).append("：").append(prev_comment.getContent()).append("<br>");
        }
        content.append(comment.getName()).append("：").append(comment.getContent());

        // 处理邮件模板
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("recipient", comment.getName());

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String time = now.format(formatter);
        context.setVariable("time", time);

        context.setVariable("content", content.toString());

        // 获取url
        String url = (String) configService.getByName("web").getValue().get("url");
        String path = String.format("%s/article/%d", url, comment.getArticleId());
        context.setVariable("url", path);

        String template = templateEngine.process("comment_email", context);

        // 如果是一级评论则邮件提醒管理员，否则邮件提醒被回复人和管理员
        String email = (prev_comment != null && !prev_comment.getEmail().isEmpty()) ? prev_comment.getEmail() : null;

        // 如果是一级评论则邮件提醒管理员，否则邮件提醒被回复人和管理员
        String emailTitle = (email != null) ? "您有最新回复~" : title;
        emailUtils.send(email, emailTitle, template);
    }

    @Override
    public void delCommentData(Integer id) {
        Comment data = commentMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该评论不存在");
        }
        commentMapper.deleteById(id);
    }

    @Override
    public void batchDelCommentData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }

    @Override
    public void editCommentData(CommentFormDTO commentFormDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentFormDTO, comment);
        commentMapper.updateById(comment);
    }

    @Override
    public CommentVO getCommentData(Integer id) {
        Comment data = commentMapper.selectById(id);

        if (data == null) {
            throw new CustomException("该评论不存在");
        }

        // 获取所有相关评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", data.getArticleId());
        List<Comment> list = commentMapper.selectList(queryWrapper);

        // 构建评论树
        data.setChildren(buildCommentTree(list, data.getId()));

        return toCommentVO(data);
    }

    @Override
    public Page<CommentVO> getCommentList(CommentFilterDTO filterVo) {
        List<Comment> list = listCommentEntities(filterVo);
        List<CommentVO> vos = list.stream().map(this::toCommentVO).collect(Collectors.toList());

        // 不传 page/size 则返回全部
        if (filterVo.getPageNum() == null || filterVo.getPageSize() == null) {
            Page<CommentVO> result = new Page<>(1, vos.size());
            result.setRecords(new ArrayList<>(vos));
            result.setTotal(vos.size());
            return result;
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNum(Math.max(1, filterVo.getPageNum()));
        pageDTO.setPageSize(Math.max(1, filterVo.getPageSize()));
        return commonUtils.getPageData(pageDTO, vos);
    }

    @Override
    public Page<CommentVO> getArticleCommentList(Integer articleId, PageDTO pageDTO) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        queryWrapper.eq("audit_status", 1);
        queryWrapper.orderByDesc("create_time");

        List<Comment> list = commentMapper.selectList(queryWrapper);

        // 构建评论树
        list = buildCommentTree(list, 0);

        List<CommentVO> vos = list.stream().map(this::toCommentVO).collect(Collectors.toList());

        // 分页处理
        return commonUtils.getPageData(pageDTO, vos);
    }

    @Override
    public void auditCommentData(Integer id) {
        Comment data = commentMapper.selectById(id);

        if (data == null) {
            throw new CustomException("该评论不存在");
        }

        data.setAuditStatus(1);
        commentMapper.updateById(data);
    }

    private List<Comment> listCommentEntities(CommentFilterDTO filterVo) {
        QueryWrapper<Comment> queryWrapper = commonUtils.queryWrapperFilter(filterVo, "name");
        queryWrapper.eq("audit_status", filterVo.getStatus());
        if (filterVo.getContent() != null && !filterVo.getContent().isEmpty()) {
            queryWrapper.like("content", filterVo.getContent());
        }

        List<Comment> list = commentMapper.selectList(queryWrapper);

        for (Comment data : list) {
            // 绑定对应的数据
            Article article = articleMapper.selectById(data.getArticleId());
            if (article != null) {
                data.setArticleTitle(article.getTitle());
            }
        }

        // 查询的结构格式
        if (Objects.equals(filterVo.getPattern(), "list")) {
            return list;
        }

        // 构建多级评论
        return buildCommentTree(list, 0);
    }

    private CommentVO toCommentVO(Comment c) {
        if (c == null) {
            return null;
        }
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(c, vo);
        vo.setChildren(new ArrayList<>());
        if (c.getChildren() != null && !c.getChildren().isEmpty()) {
            for (Comment child : c.getChildren()) {
                vo.getChildren().add(toCommentVO(child));
            }
        }
        return vo;
    }

    // 递归构建评论列表
    private List<Comment> buildCommentTree(List<Comment> list, Integer cid) {
        List<Comment> children = new ArrayList<>();

        for (Comment data : list) {
            if (data.getCommentId().equals(cid)) {
                data.setChildren(buildCommentTree(list, data.getId()));
                children.add(data);
            }
        }
        return children;
    }
}
