package liuyuyang.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@EnableAsync
@EnableScheduling
@EnableFileStorage
@SpringBootApplication
public class Main {
    @Resource
    private EnvConfigService envConfigService;
    @Resource
    private WebClient webClient;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    /**
     * 更新百度统计
     *
     */
    @Scheduled(fixedRate = 241920000)
    public void updBaiduStatistics() throws Exception {
        EnvConfig envConfig = envConfigService.getByName("baidu_statis");
        Map<String, Object> value = envConfig.getValue();
        // 构建URL
        String urlBuilder = "http://openapi.baidu.com/oauth/2.0/token" +
                "?grant_type=refresh_token" +
                "&refresh_token=" + value.get("refresh_token") +
                "&client_id=" + value.get("client_id") +
                "&client_secret=" + value.get("client_secret");
        // 发起请求
        String response = webClient.get()
                .uri(urlBuilder)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (Objects.nonNull(response)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(response, Map.class);
            value.putAll(map);
            value.put("update_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            envConfigService.updateJsonValue(envConfig.getId(), value);
        }
    }
}