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
     * 判断是否是管理员
     */
    public static boolean isAdmin() {
        String token = getHeader("Authorization");
        return parseAdminToken(token);
    }

    // 统一管理员鉴权逻辑（仅供 isAdmin 调用）
    private static boolean parseAdminToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            if (token.startsWith("Bearer ")) token = token.substring(7);
            Claims claims = JwtUtils.parseJWT(token);
            return claims != null;
        } catch (Exception e) {
            return false;
        }
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

    // 校验当前JWT是否有效
    public boolean checkToken(String token) {
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