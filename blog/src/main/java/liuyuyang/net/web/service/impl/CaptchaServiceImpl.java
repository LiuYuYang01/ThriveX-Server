package liuyuyang.net.web.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.IpUtils;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.CaptchaService;
import liuyuyang.net.web.service.EnvConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.netty.http.client.HttpClient;

import jakarta.annotation.Resource;
import java.time.Duration;

@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {
    private static final String VERIFY_URL = "https://api.hcaptcha.com/siteverify";
    private static final Duration VERIFY_TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private EnvConfigService envConfigService;

    public CaptchaServiceImpl() {
        // 独立建带超时的 WebClient，不影响全局 Bean
        HttpClient httpClient = HttpClient.create().responseTimeout(VERIFY_TIMEOUT);
        this.webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public void check(String captchaToken) {
        EnvConfig config = envConfigService.getByName("hcaptcha_key");
        // 开关关闭（或老数据未启用）视为未启用人机验证，直接放行
        if (!isEnabled(config)) return;

        String secret = secretOf(config);
        if (secret == null || secret.isBlank()) {
            log.warn("hCaptcha 已启用但未配置 secret，跳过服务端校验，请在后台补全配置");
            return;
        }

        if (captchaToken == null || captchaToken.isBlank()) {
            throw new CustomException(403, "请完成人机验证");
        }

        boolean success;
        try {
            String body = webClient.post()
                    .uri(VERIFY_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters
                            .fromFormData("secret", secret)
                            .with("response", captchaToken)
                            .with("remoteip", currentIp()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(VERIFY_TIMEOUT);
            JsonNode json = objectMapper.readTree(body);
            success = json.path("success").asBoolean(false);
            if (!success) {
                log.warn("人机验证未通过：{}", json.path("error-codes").toString());
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            // 校验服务不可用时按失败处理，避免验证环节被网络异常绕过
            log.error("调用 hCaptcha 校验接口失败", e);
            throw new CustomException(403, "人机验证服务暂不可用，请稍后再试");
        }

        if (!success) {
            throw new CustomException(403, "人机验证失败，请重新验证");
        }
    }

    /**
     * hCaptcha 是否启用：优先看 enabled 开关；
     * 老数据没有该字段时，配置过站点密钥即视为启用（兼容升级前的行为）
     */
    public static boolean isEnabled(EnvConfig config) {
        if (config == null || config.getValue() == null) return false;
        Object enabled = config.getValue().get("enabled");
        if (enabled instanceof Boolean b) return b;
        return config.getValue().get("key") instanceof String s && !s.isBlank();
    }

    // 读取配置中的服务端密钥
    private String secretOf(EnvConfig config) {
        if (config == null || config.getValue() == null) return null;
        Object secret = config.getValue().get("secret");
        return secret instanceof String s ? s : null;
    }

    private String currentIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? IpUtils.getRealIp(attrs.getRequest()) : "";
    }
}
