package liuyuyang.net.web.service;

/**
 * 人机验证（hCaptcha）服务
 */
public interface CaptchaService {

    /**
     * 校验人机验证 Token，失败直接抛出业务异常
     * <p>
     * 配置了 hcaptcha_key.secret 才启用校验；未配置则放行，兼容未启用人机验证的站点
     * <p>
     * 管理端（携带有效管理员 Token）直接放行，管理端为最高权限无需人机验证
     *
     * @param captchaToken 前端提交的 hCaptcha Token（h_captcha_response）
     */
    void check(String captchaToken);

    /**
     * 强制校验人机验证 Token，管理端也一并校验，用于登录等需要防机器人的接口
     *
     * @param captchaToken 前端提交的 hCaptcha Token（h_captcha_response）
     */
    void checkForLogin(String captchaToken);
}
