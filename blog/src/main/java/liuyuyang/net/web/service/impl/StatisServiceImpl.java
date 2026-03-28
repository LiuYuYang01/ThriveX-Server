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

import javax.annotation.Resource;
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
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private Map<String, Object> getBaiduConfig() {
        EnvConfig envConfig = envConfigService.getByName("baidu_statis");
        return envConfig.getValue();
    }

    private JsonNode callBaiduStatisticsApi(String metrics, String method, String additionalParams,
                                            String startDate, String endDate, String apiName) {
        String accessToken = (String) getBaiduConfig().get("access_token");

        if (!StringUtils.hasText(accessToken)) {
            throw new CustomException("无有效的access token");
        }

        // 处理日期参数
        String[] dates = processDateParams(startDate, endDate);
        String processedStartDate = dates[0];
        String processedEndDate = dates[1];

        try {
            // 构建URL
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(BASE_API_URL)
                    .append("?access_token=").append(accessToken)
                    .append("&site_id=").append(getBaiduConfig().get("site_id"))
                    .append("&start_date=").append(processedStartDate)
                    .append("&end_date=").append(processedEndDate)
                    .append("&metrics=").append(metrics)
                    .append("&method=").append(method);

            // 添加额外参数
            if (StringUtils.hasText(additionalParams)) {
                urlBuilder.append("&").append(additionalParams);
            }

            String url = urlBuilder.toString();
            log.info("调用{}API，URL: {}", apiName, url);

            // 发起请求
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);

                // 检查是否有错误
                if (jsonNode.has("error_code")) {
                    String errorMsg = jsonNode.get("error_msg").asText();
                    log.error("{}API调用失败: {}", apiName, errorMsg);
                    throw new CustomException("获取数据失败: " + errorMsg);
                }

                log.info("{}API调用成功", apiName);
                return jsonNode;
            }
        } catch (Exception e) {
            log.error("调用{}API失败", apiName, e);
            throw new CustomException(e.getMessage());
        }

        return null;
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
}