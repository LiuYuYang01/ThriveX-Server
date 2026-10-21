package liuyuyang.net.core.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 密码哈希工具：统一使用 BCrypt 存储，兼容存量无盐 MD5 的渐进升级。
 *
 * <p>历史版本密码用无盐 MD5 存储（安全缺陷），不能直接丢弃老数据。
 * 策略：登录时先按 BCrypt 校验；若失败且当前库里是 32 位十六进制 MD5 串，
 * 则按 MD5 校验一次，命中后立即将明文重新编码为 BCrypt 写回（渐进升级）。
 *
 * <p>BCrypt 哈希以 {@code $2a$}/{@code $2b$}/{@code $2y$} 开头且固定 60 字符，
 * 与 32 字符的 MD5 十六进制串在格式上天然可区分。
 */
public final class PasswordUtils {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    // MD5 十六进制串长度（32 位）
    private static final int MD5_HEX_LENGTH = 32;

    private PasswordUtils() {
    }

    /**
     * 编码明文密码为 BCrypt 哈希。
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验明文密码是否匹配已存储的哈希。
     *
     * @param rawPassword       用户输入的明文
     * @param storedHash        数据库中的密码哈希（可能是 BCrypt 或旧 MD5）
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        // 优先按 BCrypt 校验
        if (isBcrypt(storedHash)) {
            try {
                return ENCODER.matches(rawPassword, storedHash);
            } catch (IllegalArgumentException e) {
                // 哈希格式非法，按不匹配处理
                return false;
            }
        }

        // 兼容旧的无盐 MD5
        if (isMd5Hex(storedHash)) {
            String md5 = DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
            return md5.equalsIgnoreCase(storedHash);
        }

        return false;
    }

    /**
     * 判断存储哈希是否已是 BCrypt 格式。
     */
    public static boolean isBcrypt(String storedHash) {
        return storedHash != null
                && (storedHash.startsWith("$2a$")
                || storedHash.startsWith("$2b$")
                || storedHash.startsWith("$2y$"));
    }

    /**
     * 判断存储哈希是否是旧的无盐 MD5 十六进制串。
     */
    public static boolean isMd5Hex(String storedHash) {
        return storedHash != null
                && storedHash.length() == MD5_HEX_LENGTH
                && storedHash.matches("[0-9a-fA-F]+");
    }

    /**
     * 是否需要渐进升级：存储哈希是旧 MD5 格式时需要重新编码为 BCrypt。
     */
    public static boolean needsRehash(String storedHash) {
        return !isBcrypt(storedHash);
    }
}
