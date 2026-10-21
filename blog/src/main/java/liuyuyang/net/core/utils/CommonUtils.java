package liuyuyang.net.core.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.mapper.UserTokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommonUtils {
    @Resource
    private UserTokenMapper userTokenMapper;

    /**
     * 获取 HttpServletRequest
     * @return {HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取Header的值
     * @param name 请求头名称
     * @return 请求头
     */
    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getHeader(name);
    }

    /**
     * 判断当前请求是否为已登录的管理员。
     *
     * <p>与 {@link liuyuyang.net.core.interceptor.JwtTokenAdminInterceptor} 保持一致的鉴权标准：
     * 不仅校验 JWT 签名与有效期，还要求 token 仍存在于 user_token 表，且载荷中的 uid
     * 与 user_token 记录的 uid 一致。避免「被挤下线/过期的旧 token 仍能拿到管理员视角」的越权问题。
     */
    public boolean isAdmin() {
        String token = getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return verifyTokenAgainstStore(token);
    }

    /**
     * 校验 token 是否有效且未被吊销：JWT 签名/有效期有效 + user_token 表中存在 + uid 一致。
     */
    private boolean verifyTokenAgainstStore(String token) {
        try {
            // 先解析 JWT，拿到载荷中的 uid（签名或过期会在此抛异常）
            Claims claims = JwtUtils.parseJWT(token);
            Integer tokenUid = claims != null ? extractUid(claims) : null;

            // 校验 user_token 表中是否存在该 token
            LambdaQueryWrapper<UserToken> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserToken::getToken, token);
            List<UserToken> userTokens = userTokenMapper.selectList(wrapper);

            if (userTokens == null || userTokens.isEmpty()) {
                return false;
            }

            // 校验 token 载荷 uid 与库记录 uid 一致（防止篡改 uid 或 token 与记录错配）
            if (tokenUid == null) {
                // 兼容历史 token 无 uid 载荷：以库记录为准
                return true;
            }
            for (UserToken userToken : userTokens) {
                if (userToken.getUid() != null && userToken.getUid().equals(tokenUid)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static Integer extractUid(Claims claims) {
        Object uid = claims.get("uid");
        if (uid instanceof Integer) {
            return (Integer) uid;
        }
        if (uid instanceof Number) {
            return ((Number) uid).intValue();
        }
        return null;
    }

    /**
     * 统一分页处理：不传 page/size 则返回全部，否则进行分页。
     * <p>⚠️ 内存分页，会先把全量数据查进内存再 subList，数据量大时性能差。
     * <b>仅限</b>树形/小数据量/已内存组装的场景使用（如分类树、说说评论树）。
     * 单表列表请优先用 {@link #buildPage(PageDTO)} + mapper.selectPage 做数据库分页。
     * @return 分页结果
     */
    public static <T> Page<T> paginate(PageDTO pageDTO, List<T> list) {
        // 页码和页数只要有一个没传，就返回全部数据
        if (pageDTO == null || pageDTO.getPageNum() == null || pageDTO.getPageSize() == null) {
            Page<T> result = new Page<>(1, list.size());
            result.setRecords(new ArrayList<>(list));
            result.setTotal(list.size());
            return result;
        }

        // 否则数据分页处理
        return getPageData(pageDTO, list);
    }

    /**
     * 构造 MyBatis-Plus 分页对象，供 selectPage 做数据库分页。
     * 与 paginate 语义一致：page/size 都没传时返回全量（此时调用方需走 selectList 分支）。
     * @return Page 对象；page/size 缺失时返回 null 表示「不传分页」，由调用方决定全量查询
     */
    public static <T> Page<T> buildPage(PageDTO pageDTO) {
        if (pageDTO == null || pageDTO.getPageNum() == null || pageDTO.getPageSize() == null) {
            return null;
        }
        long pageNum = Math.max(1, pageDTO.getPageNum());
        long pageSize = Math.max(1, pageDTO.getPageSize());
        return new Page<>(pageNum, pageSize);
    }

    /** 对内存列表做分页，返回 MyBatis-Plus Page（避免 start 超出列表长度） */
    public static <T> Page<T> getPageData(PageDTO pageDTO, List<T> list) {
        // 下限保护：页码至少为 1，避免非法分页
        int page = Math.max(1, pageDTO.getPageNum() != null ? pageDTO.getPageNum() : 1);
        int size = Math.max(1, pageDTO.getPageSize() != null ? pageDTO.getPageSize() : 5);
        int total = list.size();
        int start = Math.min((page - 1) * size, total);
        int end = Math.min(start + size, total);
        List<T> pagedRecords = start >= end ? new ArrayList<>() : list.subList(start, end);

        Page<T> result = new Page<>(page, size);
        result.setRecords(pagedRecords);
        result.setTotal(total);
        return result;
    }

    public <T> QueryWrapper<T> queryWrapperDateFilter(FilterDTO filterDTO) {
        return queryWrapperDateFilter(filterDTO, new String[0]);
    }

    /**
     * 带排序的日期过滤，extraOrderDesc 里的列按倒序排在 create_time 之前（如「置顶优先」）
     */
    public <T> QueryWrapper<T> queryWrapperDateFilter(FilterDTO filterDTO, String... extraOrderDesc) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        for (String column : extraOrderDesc) {
            queryWrapper.orderByDesc(column);
        }
        queryWrapper.orderByDesc("create_time");

        // 根据开始与结束时间过滤
        if (filterDTO.getStartDate() != null && filterDTO.getEndDate() != null) {
            queryWrapper.between("create_time", filterDTO.getStartDate(), filterDTO.getEndDate());
        } else if (filterDTO.getStartDate() != null) {
            queryWrapper.ge("create_time", filterDTO.getStartDate());
        } else if (filterDTO.getEndDate() != null) {
            queryWrapper.le("create_time", filterDTO.getEndDate());
        }

        return queryWrapper;
    }

    // 校验当前JWT是否有效（与拦截器、isAdmin 共用同一套标准）
    public boolean checkToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return verifyTokenAgainstStore(token);
    }
}