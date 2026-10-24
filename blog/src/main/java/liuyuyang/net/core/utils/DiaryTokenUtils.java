package liuyuyang.net.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import liuyuyang.net.core.properties.JwtProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

/**
 * 日记访客阅读令牌：用管理员 JWT 的主秘钥派生出独立子秘钥，
 * 访客令牌访问不了任何管理接口，管理员令牌也不能当作访客令牌使用。
 */
@Component
@Slf4j
public class DiaryTokenUtils {
    private static final String SCOPE = "thrivex-diary-reader";
    private static final Duration TTL = Duration.ofDays(7);

    @Resource
    private JwtProperties jwtProperties;

    @Getter
    private long maxAge = TTL.toSeconds();

    public String issueToken() {
        return Jwts.builder()
                .claim("scope", SCOPE)
                .id(UUID.randomUUID().toString())
                .subject("diary")
                .signWith(signingKey(), Jwts.SIG.HS256)
                .expiration(new Date(System.currentTimeMillis() + TTL.toMillis()))
                .compact();
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return SCOPE.equals(claims.get("scope"));
        } catch (Exception e) {
            log.debug("日记令牌校验未通过");
            return false;
        }
    }

    private SecretKey signingKey() {
        return new SecretKeySpec(deriveKey(jwtProperties.getSecretKey()), "HmacSHA256");
    }

    /** SHA-256 派生固定 32 字节子秘钥，满足 HS256 的长度要求 */
    private static byte[] deriveKey(String masterSecret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String salted = (masterSecret == null ? "" : masterSecret) + "::thrivex-diary-reader";
            return digest.digest(salted.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("日记访问令牌秘钥派生失败", e);
        }
    }
}
