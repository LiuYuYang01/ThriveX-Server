package liuyuyang.net.web.service;

/**
 * 人机验证（hCaptcha）服务
 */
public interface CaptchaService {

    /**
     * 校验人机验证 Token，失败直接抛出业务异常
     * <p>
     * 配置了 hcaptcha_key.secret 才启用校验；未配置则放行，兼容未启用人机验证的站点
     *
     * @param captchaToken 前端提交的 hCaptcha Token（h_captcha_response）
     */
    void check(String captchaToken);
}
