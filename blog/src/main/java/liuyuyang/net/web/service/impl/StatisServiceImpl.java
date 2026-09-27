package liuyuyang.net.web.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.StatisService;
import liuyuyang.net.web.service.EnvConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class StatisServiceImpl implements StatisService {
    @Resource
    private WebClient webClient;
    @Resource
    private EnvConfigService envConfigService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_API_URL = "https://openapi.baidu.com/rest/2.0/tongji/report/getData";
    private static final String OAUTH_TOKEN_URL = "https://openapi.baidu.com/oauth/2.0/token";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private Map<String, Object> getBaiduConfig() {
        EnvConfig envConfig = envConfigService.getByName("baidu_statis");
        return envConfig.getValue();
    }

    private JsonNode callBaiduStatisticsApi(String metrics, String method, String additionalParams,
                                            String startDate, String endDate, String apiName) {
        JsonNode jsonNode = requestBaiduApi(metrics, method, additionalParams, startDate, endDate, apiName);

        // token 失效（110 无效 / 111 过期）时自动刷新并重试一次
        if (jsonNode != null && isTokenInvalid(jsonNode)) {
            log.warn("{}返回 access_token 已失效，自动刷新后重试", apiName);
            refreshAccessToken();
            jsonNode = requestBaiduApi(metrics, method, additionalParams, startDate, endDate, apiName);
        }

        if (jsonNode != null) {
            // 检查是否有错误（部分报表成功响应也带 error_code:0，视为成功）
            if (jsonNode.has("error_code") && jsonNode.get("error_code").asInt() != 0) {
                int errorCode = jsonNode.get("error_code").asInt();
                String errorMsg = jsonNode.has("error_msg") ? jsonNode.get("error_msg").asText() : "响应: " + jsonNode;
                log.error("{}API调用失败: code={}, {}", apiName, errorCode, errorMsg);
                throw new CustomException("获取数据失败(code=" + errorCode + "): " + errorMsg);
            }

            log.info("{}API调用成功", apiName);
            return jsonNode;
        }

        return null;
    }

    private JsonNode requestBaiduApi(String metrics, String method, String additionalParams,
                                     String startDate, String endDate, String apiName) {
        String accessToken = (String) getBaiduConfig().get("access_token");

        if (!StringUtils.hasText(accessToken)) {
            throw new CustomException("无有效的access token");
        }

        // 处理日期参数
        String[] dates = processDateParams(startDate, endDate);

        try {
            // 构建URL
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(BASE_API_URL)
                    .append("?access_token=").append(accessToken)
                    .append("&site_id=").append(getBaiduConfig().get("site_id"))
                    .append("&start_date=").append(dates[0])
                    .append("&end_date=").append(dates[1])
                    .append("&metrics=").append(metrics)
                    .append("&method=").append(method);

            // 添加额外参数
            if (StringUtils.hasText(additionalParams)) {
                urlBuilder.append("&").append(additionalParams);
            }

            String url = urlBuilder.toString();

            // 发起请求
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null) {
                return objectMapper.readTree(response);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            // 底层异常（如 WebClient 报错）可能含带 access_token 的完整 URL，不能直接透传
            log.error("调用{}API失败", apiName, e);
            throw new CustomException("调用百度统计接口失败，请稍后再试");
        }

        return null;
    }

    // 110: access_token 无效；111: access_token 已过期；用 path 兜底，成功响应可能不带 error_code
    private boolean isTokenInvalid(JsonNode node) {
        int code = node.path("error_code").asInt(0);
        return code == 110 || code == 111;
    }

    /**
     * 用 refresh_token 换取新的 access_token 并写回配置
     * 百度 OAuth 的 refresh_token 单次有效，刷新后必须同步保存返回的最新值
     */
    public synchronized void refreshAccessToken() {
        Map<String, Object> config = getBaiduConfig();
        String refreshToken = (String) config.get("refresh_token");
        String clientId = (String) config.get("client_id");
        String clientSecret = (String) config.get("client_secret");

        if (!StringUtils.hasText(refreshToken) || !StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            throw new CustomException("缺少 refresh_token/client_id/client_secret 配置，请在第三方设置中补全以启用自动刷新");
        }

        String response;
        try {
            response = webClient.get()
                    .uri(OAUTH_TOKEN_URL
                            + "?grant_type=refresh_token"
                            + "&refresh_token=" + refreshToken
                            + "&client_id=" + clientId
                            + "&client_secret=" + clientSecret)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            // 异常信息可能含带 client_secret 的完整 URL，不能直接透传
            log.error("刷新百度统计 access_token 失败", e);
            throw new CustomException("刷新百度统计 token 失败，请稍后再试");
        }

        try {
            JsonNode result = objectMapper.readTree(response);
            String newAccessToken = result.has("access_token") ? result.get("access_token").asText() : null;
            String newRefreshToken = result.has("refresh_token") ? result.get("refresh_token").asText() : null;

            if (!StringUtils.hasText(newAccessToken)) {
                String desc = result.has("error_description") ? result.get("error_description").asText() : "未知错误";
                log.error("刷新百度统计 token 失败：{}", desc);
                throw new CustomException("刷新 token 失败，授权可能已被撤销，请重新授权百度统计");
            }

            EnvConfig baiduConfig = envConfigService.getByName("baidu_statis");
            envConfigService.updateJsonFieldValue(baiduConfig.getId(), "access_token", newAccessToken);
            if (StringUtils.hasText(newRefreshToken)) {
                envConfigService.updateJsonFieldValue(baiduConfig.getId(), "refresh_token", newRefreshToken);
            }
            log.info("百度统计 access_token 已自动刷新");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析百度统计刷新响应失败", e);
            throw new CustomException("刷新百度统计 token 失败，请稍后再试");
        }
    }

    /**
     * 定时保活：access_token 有效期约 30 天，每周一凌晨刷新一次
     */
    @Scheduled(cron = "0 0 4 ? * MON")
    public void refreshBaiduToken() {
        try {
            refreshAccessToken();
        } catch (CustomException e) {
            // 未配置 refresh_token 或授权已失效时降级为告警，不影响站点运行
            log.warn("定时刷新百度统计 token 跳过：{}", e.getMessage());
        }
    }

    /**
     * 处理日期参数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 处理后的日期数组 [startDate, endDate]
     */
    private String[] processDateParams(String startDate, String endDate) {
        String today = LocalDateTime.now().format(DATE_FORMAT);

        if (!StringUtils.hasText(startDate)) {
            startDate = today;
        }
        if (!StringUtils.hasText(endDate)) {
            endDate = today;
        }

        return new String[]{startDate, endDate};
    }

    @Override
    public JsonNode getStatisData(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count,ip_count",
                "overview/getTimeTrendRpt",
                null,
                startDate,
                endDate,
                "基础统计数据"
        );
    }

    @Override
    public JsonNode getOverviewTimeTrend(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count,ip_count,bounce_ratio,avg_visit_time",
                "overview/getTimeTrendRpt",
                null,
                startDate,
                endDate,
                "概览时间趋势报表"
        );
    }

    @Override
    public JsonNode getNewVisitorTrend(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "new_visitor_count,new_visitor_ratio",
                "trend/time/a",
                "gran=day&area=",
                startDate,
                endDate,
                "新访客趋势报表"
        );
    }

    @Override
    public JsonNode getBasicOverviewTrend(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count,ip_count",
                "overview/getTimeTrendRpt",
                null,
                startDate,
                endDate,
                "基础概览时间趋势报表"
        );
    }

    @Override
    public JsonNode getRegionReport(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count",
                "visit/area/a",
                null,
                startDate,
                endDate,
                "地域分布报表"
        );
    }

    @Override
    public JsonNode getSourceReport(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count",
                "visit/source/all/a",
                null,
                startDate,
                endDate,
                "来源分布报表"
        );
    }

    @Override
    public JsonNode getClientReport(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count",
                "visit/client/a",
                null,
                startDate,
                endDate,
                "设备分布报表"
        );
    }
}