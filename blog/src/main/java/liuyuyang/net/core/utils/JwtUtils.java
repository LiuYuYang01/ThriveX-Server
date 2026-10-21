package liuyuyang.net.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import liuyuyang.net.core.properties.JwtProperties;
import lombok.Getter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    // JWT 载荷中用户 ID 的 key
    public static final String CLAIM_UID = "uid";
    // JWT 载荷中令牌类型的 key（access / refresh）
    public static final String CLAIM_TYPE = "type";

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    @Getter
    private static JwtProperties jwtProperties;

    public static void setJwtProperties(JwtProperties properties) {
        if (JwtUtils.jwtProperties == null) {
            JwtUtils.jwtProperties = properties;
        }
    }

    /**
     * 生成访问令牌（携带 uid，短有效期）。
     *
     * @param uid    用户 ID
     * @return access token
     */
    public static String createAccessToken(Integer uid) {
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put(CLAIM_UID, uid);
        claims.put(CLAIM_TYPE, TYPE_ACCESS);
        return createJWT(getJwtProperties().getSecretKey(), getJwtProperties().getTtl(), claims);
    }

    /**
     * 生成刷新令牌（携带 uid，长有效期，仅用于换取新 access token）。
     *
     * @param uid    用户 ID
     * @return refresh token
     */
    public static String createRefreshToken(Integer uid) {
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put(CLAIM_UID, uid);
        claims.put(CLAIM_TYPE, TYPE_REFRESH);
        return createJWT(getJwtProperties().getSecretKey(), getRefreshTtl(), claims);
    }

    /**
     * 生成 jwt（保留原签名，供需要自定义载荷的场景使用）
     * 使用 HS256 算法，密钥使用固定密钥
     *
     * @param claims 设置的信息
     * @return token
     */
    public static String createJWT(Map<String, Object> claims) {
        return createJWT(getJwtProperties().getSecretKey(), getJwtProperties().getTtl(), claims);
    }

    /**
     * 生成 jwt
     * 使用 HS256 算法，密钥使用固定密钥
     * jjwt 0.12 要求 HS256 密钥至少 32 字节，过短的密钥会抛 WeakKeyException
     *
     * @param secretKey jwt 密钥
     * @param ttlMillis jwt 过期时间(毫秒)
     * @param claims    设置的信息
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 生成 JWT 的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;

        // 设置 jwt 的 body
        return Jwts.builder()
                .claims(claims)
                .signWith(hmacKey(secretKey), Jwts.SIG.HS256)
                .expiration(new Date(expMillis))
                .compact();
    }

    /**
     * 获取签名密钥
     */
    public static byte[] getSigningKey() {
        return getJwtProperties().getSecretKey().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Token 解密（解析并校验签名与过期时间）
     *
     * @param token 加密后的 token
     */
    public static Claims parseJWT(String token) {
        return Jwts.parser()
                .verifyWith(hmacKey(getJwtProperties().getSecretKey()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 token 载荷中读取用户 ID。
     *
     * @param token access/refresh token
     * @return 用户 ID，无 uid 载荷时返回 null
     */
    public static Integer parseUid(String token) {
        Claims claims = parseJWT(token);
        Object uid = claims.get(CLAIM_UID);
        if (uid instanceof Integer) {
            return (Integer) uid;
        }
        if (uid instanceof Number) {
            return ((Number) uid).intValue();
        }
        return null;
    }

    private static long getRefreshTtl() {
        long ttl = jwtProperties.getRefreshTtl();
        // 未配置 refreshTtl 时回退到 access ttl（保证向后兼容旧配置）
        return ttl > 0 ? ttl : jwtProperties.getTtl();
    }

    private static SecretKey hmacKey(String secretKey) {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

}
