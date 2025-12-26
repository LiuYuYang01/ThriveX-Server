package liuyuyang.net.web.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 地图工具
 *
 * @author luoyuanxiang
 */
public class AmapLocationUtil {

    /**
     * 通过IP地址调用高德API获取地理位置
     */
    public static Map<String, String> getLocationByIp(String ip, String key) {
        Map<String, String> result = new HashMap<>();
        result.put("province", "未知");
        result.put("city", "未知");

        if (ip == null || ip.isEmpty()) {
            return result;
        }

        try {
            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("ip", ip);
            params.put("key", key);

            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    buildUrlWithParams(params),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 解析响应结果
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                if (responseBody != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    String status = jsonNode.get("status").textValue();

                    // 状态为1表示成功
                    if (Objects.equals("1",  status)) {
                        String province = jsonNode.get("province").textValue();
                        String city = jsonNode.get("city").textValue();
                        if (StrUtil.isNotBlank(province)) {
                            result.put("province", province);
                        }
                        if (StrUtil.isNotBlank(city)) {
                            result.put("city", city);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 异常处理，记录日志等
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 构建带参数的URL
     */
    private static String buildUrlWithParams(Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v3/ip");
        urlBuilder.append("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }

        // 移除最后一个&
        if (urlBuilder.charAt(urlBuilder.length() - 1) == '&') {
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        return urlBuilder.toString();
    }
}
