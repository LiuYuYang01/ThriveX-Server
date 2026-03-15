package liuyuyang.net.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.vo.FilterVo;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.web.mapper.UserTokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CommonUtils {
    @Resource
    private UserTokenMapper userTokenMapper;

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取Header的值
     *
     * @param name 请求头名称
     * @return 请求头
     */
    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        return Objects.requireNonNull(request).getHeader(name);
    }

    /**
     * 判断是否是管理员
     */
    public static boolean isAdmin() {
        String token = getHeader("Authorization");
        return isAdmin(token);
    }

    // 鉴权：判断是否为超级管理员
    public static boolean isAdmin(String token) {
        if (token != null) {
            if (token.startsWith("Bearer ")) token = token.substring(7);
            Claims claims = JwtUtils.parseJWT(token);
            return claims != null;
        }

        return false;
    }

    /** 对内存列表做分页，返回 MyBatis-Plus Page（避免 start 超出列表长度） */
    public <T> Page<T> getPageData(PageVo pageVo, List<T> list) {
        // 下限保护：页码至少为 1，避免非法分页
        int page = Math.max(1, pageVo.getPage() != null ? pageVo.getPage() : 1);
        int size = Math.max(1, pageVo.getSize() != null ? pageVo.getSize() : 5);
        int total = list.size();
        int start = Math.min((page - 1) * size, total);
        int end = Math.min(start + size, total);
        List<T> pagedRecords = start >= end ? new ArrayList<>() : list.subList(start, end);

        Page<T> result = new Page<>(page, size);
        result.setRecords(pagedRecords);
        result.setTotal(total);
        return result;
    }

    // 过滤数据
    public <T> QueryWrapper<T> queryWrapperFilter(FilterVo filterVo) {
        return queryWrapperFilter(filterVo, "title");
    }

    public <T> QueryWrapper<T> queryWrapperFilter(FilterVo filterVo, String key) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        // 根据关键字通过标题过滤出对应数据
        if (filterVo.getKey() != null && !filterVo.getKey().isEmpty()) {
            queryWrapper.like(key, "%" + filterVo.getKey() + "%");
        }

        // 根据开始与结束时间过滤
        if (filterVo.getStartDate() != null && filterVo.getEndDate() != null) {
            queryWrapper.between("create_time", filterVo.getStartDate(), filterVo.getEndDate());
        } else if (filterVo.getStartDate() != null) {
            queryWrapper.ge("create_time", filterVo.getStartDate());
        } else if (filterVo.getEndDate() != null) {
            queryWrapper.le("create_time", filterVo.getEndDate());
        }

        return queryWrapper;
    }

    // 校验当前JWT是否有效
    public boolean check(String token) {
        try {
            if (token != null) {
                if (token.startsWith("Bearer ")) token = token.substring(7);

                LambdaQueryWrapper<UserToken> userTokenLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userTokenLambdaQueryWrapper.eq(UserToken::getToken, token);
                List<UserToken> userTokens = userTokenMapper.selectList(userTokenLambdaQueryWrapper);

                // 如果跟之前的token相匹配则进一步判断token是否有效
                if (userTokens != null && !userTokens.isEmpty()) {
                    JwtUtils.parseJWT(token);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}