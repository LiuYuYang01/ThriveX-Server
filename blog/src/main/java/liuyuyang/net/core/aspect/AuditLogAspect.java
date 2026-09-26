package liuyuyang.net.core.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liuyuyang.net.core.annotation.AuditLog;
import liuyuyang.net.core.properties.JwtProperties;
import liuyuyang.net.core.utils.IpUtils;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.OperationLog;
import liuyuyang.net.model.User;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.web.mapper.UserMapper;
import liuyuyang.net.web.mapper.UserTokenMapper;
import liuyuyang.net.web.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操作日志切面
 * 自动记录管理端的写操作（POST/PUT/PATCH/DELETE），前台免 Token 接口不记录；
 * 也可通过 @AuditLog 注解显式标注需要强制记录的接口（如登录）
 */
@Aspect
@Component
@Slf4j
public class AuditLogAspect {
    // 参数/错误信息的最大记录长度，防止正文类大参数撑爆日志表
    private static final int MAX_CONTENT_LENGTH = 2000;

    @Resource
    private OperationLogService operationLogService;
    @Resource
    private UserTokenMapper userTokenMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private ObjectMapper objectMapper;

    @Around("within(liuyuyang.net.web.controller..*) && !@annotation(liuyuyang.net.core.annotation.NoTokenRequired) "
            + "|| @annotation(liuyuyang.net.core.annotation.AuditLog)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String httpMethod = request.getMethod().toUpperCase();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditLog auditLog = method.getAnnotation(AuditLog.class);

        // 只记录管理端写请求；显式标注 @AuditLog 的接口（如登录）强制记录
        boolean isWriteMethod = "POST".equals(httpMethod) || "PUT".equals(httpMethod)
                || "PATCH".equals(httpMethod) || "DELETE".equals(httpMethod);
        if (!isWriteMethod && auditLog == null) {
            return joinPoint.proceed();
        }

        OperationLog operationLog = buildLog(request, httpMethod, joinPoint, auditLog);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            operationLog.setElapsed((int) (System.currentTimeMillis() - start));
            operationLog.setStatus(1);
            operationLogService.recordLog(operationLog);
            return result;
        } catch (Throwable e) {
            operationLog.setElapsed((int) (System.currentTimeMillis() - start));
            operationLog.setStatus(0);
            operationLog.setErrorMsg(truncate(e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
            operationLogService.recordLog(operationLog);
            throw e;
        }
    }

    private OperationLog buildLog(HttpServletRequest request, String httpMethod, ProceedingJoinPoint joinPoint, AuditLog auditLog) {
        OperationLog operationLog = new OperationLog();
        String module = auditLog != null && !auditLog.module().isEmpty() ? auditLog.module() : resolveModule(request.getRequestURI());
        operationLog.setModule(module);
        operationLog.setType(auditLog != null && !auditLog.type().isEmpty() ? auditLog.type() : resolveType(httpMethod));
        operationLog.setDescription(auditLog != null ? auditLog.description() : "");
        operationLog.setMethod(httpMethod);
        operationLog.setUrl(truncate(request.getRequestURI(), 255));
        operationLog.setIp(IpUtils.getRealIp(request));
        operationLog.setStatus(1);
        operationLog.setElapsed(0);
        operationLog.setCreateTime(System.currentTimeMillis());
        operationLog.setParams(maskSensitiveParams(serializeParams(joinPoint.getArgs())));
        operationLog.setUsername(resolveUsername(request, joinPoint.getArgs()));
        return operationLog;
    }

    private String resolveType(String httpMethod) {
        return switch (httpMethod) {
            case "DELETE" -> "删除";
            case "PUT", "PATCH" -> "修改";
            default -> "操作";
        };
    }

    // 模块名按 URL 前缀匹配，长前缀优先（如 record/comment 先于 record）
    private static final java.util.Map<String, String> MODULE_MAPPINGS = new java.util.LinkedHashMap<>() {{
        put("record/comment", "说说评论管理");
        put("record", "闪念管理");
        put("article", "文章管理");
        put("cate", "分类管理");
        put("tag", "标签管理");
        put("comment", "评论管理");
        put("wall", "留言管理");
        put("link", "友链管理");
        put("swiper", "轮播图管理");
        put("footprint", "足迹管理");
        put("milestone", "里程碑管理");
        put("user", "用户管理");
        put("web_config", "系统配置");
        put("page_config", "页面配置");
        put("env_config", "第三方配置");
        put("file", "文件管理");
        put("assistant", "AI助手管理");
        put("email", "邮件管理");
        put("operation_log", "操作日志管理");
        put("rss", "鱼塘管理");
    }};

    /**
     * 从请求地址推断操作模块，如 /api/article/1 → 文章管理
     */
    private String resolveModule(String uri) {
        if (uri == null) return "";
        String path = uri;
        for (String prefix : new String[]{"/api/", "/"}) {
            if (path.startsWith(prefix)) {
                path = path.substring(prefix.length());
                break;
            }
        }
        for (java.util.Map.Entry<String, String> entry : MODULE_MAPPINGS.entrySet()) {
            if (path.equals(entry.getKey()) || path.startsWith(entry.getKey() + "/")) {
                return entry.getValue();
            }
        }
        return truncate(path, 50);
    }

    /**
     * 解析操作人：优先从请求 Token 反查用户，登录等无 Token 场景回退到请求参数中的用户名
     */
    private String resolveUsername(HttpServletRequest request, Object[] args) {
        String username = resolveUsernameFromToken(request);
        if (username != null) return username;

        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof UserLoginDTO userLoginDTO) {
                    return userLoginDTO.getUsername();
                }
            }
        }
        return "未知";
    }

    private String resolveUsernameFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader(jwtProperties.getTokenName());
            if (token == null || token.trim().isEmpty()) return null;
            if (token.startsWith("Bearer ")) token = token.substring(7);

            LambdaQueryWrapper<UserToken> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserToken::getToken, token);
            List<UserToken> userTokens = userTokenMapper.selectList(wrapper);
            if (userTokens == null || userTokens.isEmpty()) return null;

            User user = userMapper.selectById(userTokens.get(0).getUid());
            return user != null ? user.getUsername() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 序列化请求参数，跳过 Servlet 对象/流等无法序列化的类型，文件类型仅记录文件名和大小
     */
    private String serializeParams(Object[] args) {
        if (args == null || args.length == 0) return null;

        List<Object> serializableArgs = new ArrayList<>();
        for (Object arg : args) {
            if (arg == null) continue;
            if (arg instanceof MultipartFile file) {
                java.util.Map<String, Object> fileInfo = new java.util.LinkedHashMap<>();
                fileInfo.put("filename", file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
                fileInfo.put("size", file.getSize());
                serializableArgs.add(fileInfo);
            } else if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                    || arg instanceof InputStream || arg instanceof OutputStream) {
                continue;
            } else {
                serializableArgs.add(arg);
            }
        }
        if (serializableArgs.isEmpty()) return null;

        try {
            return truncate(objectMapper.writeValueAsString(serializableArgs));
        } catch (Exception e) {
            return truncate(Arrays.toString(serializableArgs.toArray()));
        }
    }

    /**
     * 脱敏参数中的密码类字段
     */
    private String maskSensitiveParams(String params) {
        if (params == null) return null;
        return params.replaceAll("(\"\\w*[pP]assword\\w*\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
    }

    private String truncate(String content) {
        return truncate(content, MAX_CONTENT_LENGTH);
    }

    private String truncate(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
}
