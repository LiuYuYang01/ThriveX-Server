package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.comment.CommentFilterDTO;
import liuyuyang.net.dto.comment.CommentFormDTO;
import liuyuyang.net.enums.comment.CommentPatternEnum;
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

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // 前台匿名提交一律置为待审核，请求体中的 status 不生效
        saveCommentData(commentFormDTO, 0);
    }

    @Override
    public void replyCommentData(CommentFormDTO commentFormDTO) throws Exception {
        // 管理端回复无需审核，落库即通过
        saveCommentData(commentFormDTO, 1);
    }

    private void saveCommentData(CommentFormDTO commentFormDTO, int status) throws Exception {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentFormDTO, comment);
        comment.setStatus(status);
        commentMapper.insert(comment);

        // 文章标题
        String title = articleMapper.selectById(comment.getArticleId()).getTitle();

        // 评论记录
        StringBuilder content = new StringBuilder();
        // 判断是否还有上一条评论
        Comment prev_comment = null;
        if (comment.getCommentId() != null && comment.getCommentId() != 0) {
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
        List<CommentVO> children = buildCommentTreeVO(list, data.getId());
        CommentVO vo = toCommentVO(data);
        vo.setArticleTitle(articleTitleOf(data.getArticleId()));
        vo.setChildren(children);
        return vo;
    }

    @Override
    public Page<CommentVO> getCommentList(CommentFilterDTO commentFilterDTO) {
        // LIST 模式：平铺列表可直接数据库分页
        if (commentFilterDTO.getPattern() == CommentPatternEnum.LIST) {
            QueryWrapper<Comment> queryWrapper = buildCommentQueryWrapper(commentFilterDTO);
            Page<Comment> page = CommonUtils.buildPage(commentFilterDTO);
            List<Comment> flat;
            Page<CommentVO> result;
            if (page == null) {
                flat = commentMapper.selectList(queryWrapper);
                result = new Page<>(1, flat.size());
                result.setTotal(flat.size());
            } else {
                Page<Comment> entityPage = commentMapper.selectPage(page, queryWrapper);
                flat = entityPage.getRecords();
                result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
                result.setTotal(entityPage.getTotal());
            }
            List<CommentVO> vos = flat.stream().map(c -> {
                CommentVO vo = toCommentVO(c);
                vo.setArticleTitle(articleTitleOf(c.getArticleId()));
                return vo;
            }).collect(Collectors.toList());
            fillArticleTitles(vos); // 复用批量标题回填逻辑
            result.setRecords(vos);
            return result;
        }

        // TREE 模式：一级评论做数据库分页，子评论按当页顶层 id 批量查出后挂载
        return getCommentTreePage(commentFilterDTO);
    }

    // TREE 模式：顶层评论 DB 分页 + 子评论批量挂载
    private Page<CommentVO> getCommentTreePage(CommentFilterDTO commentFilterDTO) {
        QueryWrapper<Comment> rootWrapper = buildCommentQueryWrapper(commentFilterDTO);
        // 仅查一级评论
        rootWrapper.and(w -> w.isNull("comment_id").or().eq("comment_id", 0));

        Page<Comment> page = CommonUtils.buildPage(commentFilterDTO);
        List<Comment> roots;
        Page<CommentVO> result;
        long total;
        if (page == null) {
            roots = commentMapper.selectList(rootWrapper);
            total = roots.size();
            result = new Page<>(1, (int) Math.max(1, total));
        } else {
            Page<Comment> entityPage = commentMapper.selectPage(page, rootWrapper);
            roots = entityPage.getRecords();
            total = entityPage.getTotal();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
        }
        result.setTotal(total);

        // 批量查当页顶层评论的所有子评论
        List<CommentVO> vos = buildTreeWithChildren(roots);
        fillArticleTitles(vos);
        result.setRecords(vos);
        return result;
    }

    // 给定顶层评论列表，批量查其子评论并构建树
    private List<CommentVO> buildTreeWithChildren(List<Comment> roots) {
        if (roots == null || roots.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> rootIds = roots.stream().map(Comment::getId).collect(Collectors.toList());

        QueryWrapper<Comment> childWrapper = new QueryWrapper<>();
        childWrapper.in("comment_id", rootIds);
        childWrapper.eq("status", 1);
        childWrapper.orderByAsc("create_time");
        List<Comment> children = commentMapper.selectList(childWrapper);

        // 扁平列表按顶层评论分组，递归挂载（评论最多两层：一级 + 回复，这里复用现有树构建逻辑）
        List<Comment> all = new ArrayList<>(roots);
        all.addAll(children);

        List<CommentVO> vos = buildCommentTreeVO(all, 0);
        // 只保留当页的顶层评论
        return vos;
    }

    private QueryWrapper<Comment> buildCommentQueryWrapper(CommentFilterDTO commentFilterDTO) {
        QueryWrapper<Comment> queryWrapper = commonUtils.queryWrapperDateFilter(commentFilterDTO);
        if (commonUtils.isAdmin()) {
            // 管理员可按状态筛选，不传则查看全部
            if (commentFilterDTO.getStatus() != null) {
                queryWrapper.eq("status", commentFilterDTO.getStatus());
            }
        } else {
            // 非管理员强制只看已审核，防止翻看待审核内容
            queryWrapper.eq("status", 1);
        }

        if (commentFilterDTO.getContent() != null) {
            queryWrapper.like("content", "%" + commentFilterDTO.getContent() + "%");
        }
        return queryWrapper;
    }

    @Override
    public Page<CommentVO> getArticleCommentList(Integer articleId, PageDTO pageDTO) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        // 仅查一级评论
        queryWrapper.and(w -> w.isNull("comment_id").or().eq("comment_id", 0));

        Page<Comment> page = CommonUtils.buildPage(pageDTO);
        List<Comment> roots;
        Page<CommentVO> result;
        long total;
        if (page == null) {
            roots = commentMapper.selectList(queryWrapper);
            total = roots.size();
            result = new Page<>(1, (int) Math.max(1, total));
        } else {
            Page<Comment> entityPage = commentMapper.selectPage(page, queryWrapper);
            roots = entityPage.getRecords();
            total = entityPage.getTotal();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
        }
        result.setTotal(total);

        // 批量挂载子评论
        List<CommentVO> vos = buildTreeWithChildren(roots);
        fillArticleTitles(vos);
        result.setRecords(vos);
        return result;
    }

    @Override
    public void auditCommentData(Integer id) {
        Comment data = commentMapper.selectById(id);

        if (data == null) {
            throw new CustomException("该评论不存在");
        }

        data.setStatus(1);
        commentMapper.updateById(data);
    }

    private List<Comment> queryFlatComments(CommentFilterDTO commentFilterDTO) {
        QueryWrapper<Comment> queryWrapper = commonUtils.queryWrapperDateFilter(commentFilterDTO);
        if (commonUtils.isAdmin()) {
            // 管理员可按状态筛选，不传则查看全部
            if (commentFilterDTO.getStatus() != null) {
                queryWrapper.eq("status", commentFilterDTO.getStatus());
            }
        } else {
            // 非管理员强制只看已审核，防止翻看待审核内容
            queryWrapper.eq("status", 1);
        }

        if (commentFilterDTO.getContent() != null) {
            queryWrapper.like("content", "%" + commentFilterDTO.getContent() + "%");
        }

        return commentMapper.selectList(queryWrapper);
    }

    private void fillArticleTitles(List<CommentVO> nodes) {
        // 先收集树中所有评论的文章 id，再一次性批量查标题，消除 articleTitleOf 的 N+1 查询
        List<Integer> articleIds = new ArrayList<>();
        collectArticleIds(nodes, articleIds);
        if (articleIds.isEmpty()) {
            return;
        }
        Map<Integer, String> titleMap = new HashMap<>();
        articleMapper.selectByIds(articleIds.stream().distinct().collect(Collectors.toList()))
                .forEach(a -> titleMap.put(a.getId(), a.getTitle()));
        fillArticleTitlesFromMap(nodes, titleMap);
    }

    private void collectArticleIds(List<CommentVO> nodes, List<Integer> articleIds) {
        for (CommentVO vo : nodes) {
            if (vo.getArticleId() != null) {
                articleIds.add(vo.getArticleId());
            }
            if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
                collectArticleIds(vo.getChildren(), articleIds);
            }
        }
    }

    private void fillArticleTitlesFromMap(List<CommentVO> nodes, Map<Integer, String> titleMap) {
        for (CommentVO vo : nodes) {
            vo.setArticleTitle(titleMap.get(vo.getArticleId()));
            if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
                fillArticleTitlesFromMap(vo.getChildren(), titleMap);
            }
        }
    }

    private String articleTitleOf(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        return article != null ? article.getTitle() : null;
    }

    private CommentVO toCommentVO(Comment c) {
        if (c == null) {
            return null;
        }
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(c, vo);
        return vo;
    }

    // 递归构建评论列表
    private List<CommentVO> buildCommentTreeVO(List<Comment> flat, Integer parentId) {
        List<CommentVO> children = new ArrayList<>();

        for (Comment data : flat) {
            if (Objects.equals(data.getCommentId(), parentId)) {
                CommentVO vo = toCommentVO(data);
                vo.setChildren(buildCommentTreeVO(flat, data.getId()));
                children.add(vo);
            }
        }
        return children;
    }
}
