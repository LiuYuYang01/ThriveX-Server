package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.EmailUtils;
import liuyuyang.net.common.utils.YuYangUtils;
import liuyuyang.net.model.Article;
import liuyuyang.net.model.Comment;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.comment.CommentFilterVo;
import liuyuyang.net.web.mapper.ArticleMapper;
import liuyuyang.net.web.mapper.CommentMapper;
import liuyuyang.net.web.service.CommentService;
import liuyuyang.net.web.service.EnvConfigService;
import liuyuyang.net.web.service.WebConfigService;
import liuyuyang.net.web.utils.AmapLocationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private EmailUtils emailUtils;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private YuYangUtils yuYangUtils;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private WebConfigService configService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private EnvConfigService envConfigService;

    @Override
    public void add(Comment comment) throws Exception {
        String agent = request.getHeader("User-Agent");
        //解析agent字符串
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        //获取浏览器对象
        Browser browser = userAgent.getBrowser();
        //获取操作系统对象
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        comment.setUserAgent(agent);
        comment.setOs(operatingSystem.getName());
        comment.setBrowser(browser.getName());
        comment.setIp(getRealIpAddress());
        EnvConfig envConfig = envConfigService.getByName("gaode_coordinate");
        Map<String, Object> value = envConfig.getValue();
        Map<String, String> map = AmapLocationUtil.getLocationByIp(getRealIpAddress(), value.get("key").toString());
        comment.setProvince(map.get("province"));
        comment.setCity(map.get("city"));
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

    /**
     * 获取用户真实IP地址
     * 考虑代理、负载均衡等场景
     */
    private String getRealIpAddress() {
        // 常用的代理IP请求头
        String[] headers = {
                "x-forwarded-for",    // 一般用于记录代理信息
                "Proxy-Client-IP",    // Apache代理常用
                "WL-Proxy-Client-IP", // WebLogic代理常用
                "HTTP_CLIENT_IP",     // 部分代理使用
                "HTTP_X_FORWARDED_FOR" // 部分代理使用
        };

        String ipAddress = null;
        for (String header : headers) {
            ipAddress = request.getHeader(header);
            if (isValidIp(ipAddress)) {
                break;
            }
        }

        // 如果以上请求头都没有获取到，则使用remoteAddr
        if (!isValidIp(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            // 处理本地地址情况
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 获取本机真实IP
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    // 无法获取本机IP时保持默认值
                }
            }
        }

        // 对于通过多个代理的情况，取第一个有效IP
        if (ipAddress != null && ipAddress.contains(",")) {
            String[] ips = ipAddress.split(",");
            for (String ip : ips) {
                if (isValidIp(ip.trim())) {
                    return ip.trim();
                }
            }
        }

        return ipAddress;
    }

    /**
     * 验证IP地址是否有效
     */
    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }


    @Override
    public Comment get(Integer id) {
        Comment data = commentMapper.selectById(id);

        if (data == null) {
            throw new CustomException(400, "该评论不存在");
        }

        // 获取所有相关评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", data.getArticleId());
        List<Comment> list = commentMapper.selectList(queryWrapper);

        // 构建评论树
        data.setChildren(buildCommentTree(list, data.getId()));

        return data;
    }

    @Override
    public List<Comment> list(CommentFilterVo filterVo) {
        QueryWrapper<Comment> queryWrapper = yuYangUtils.queryWrapperFilter(filterVo, "name");
        queryWrapper.eq("audit_status", filterVo.getStatus());
        if (filterVo.getContent() != null && !filterVo.getContent().isEmpty()) {
            queryWrapper.like("content", filterVo.getContent());
        }

        List<Comment> list = commentMapper.selectList(queryWrapper);

        for (Comment data : list) {
            // 绑定对应的数据
            Article article = articleMapper.selectById(data.getArticleId());
            if (article != null) data.setArticleTitle(article.getTitle());
        }

        // 查询的结构格式
        if (Objects.equals(filterVo.getPattern(), "list")) return list;

        // 构建多级评论
        return buildCommentTree(list, 0);
    }

    @Override
    public Page<Comment> paging(CommentFilterVo filterVo, PageVo pageVo) {
        List<Comment> list = list(filterVo);
        return yuYangUtils.getPageData(pageVo, list);
    }

    @Override
    public Page<Comment> getArticleCommentList(Integer articleId, PageVo pageVo) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        queryWrapper.eq("audit_status", 1);
        queryWrapper.orderByDesc("create_time");

        List<Comment> list = commentMapper.selectList(queryWrapper);

        // 构建评论树
        list = buildCommentTree(list, 0);

        // 分页处理
        return yuYangUtils.getPageData(pageVo, list);
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
