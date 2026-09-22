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
    @Getter
    private static JwtProperties jwtProperties;

    public static void setJwtProperties(JwtProperties properties) {
        if (JwtUtils.jwtProperties == null) {
            JwtUtils.jwtProperties = properties;
        }
    }

    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param claims 设置的信息
     * @return
     */
    public static String createJWT(Map<String, Object> claims) {
        return createJWT(getJwtProperties().getSecretKey(), getJwtProperties().getTtl(), claims);
    }

    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     * jjwt 0.12 要求 HS256 秘钥至少 32 字节，过短的秘钥会抛 WeakKeyException
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;

        // 设置jwt的body
        return Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .claims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(hmacKey(secretKey), Jwts.SIG.HS256)
                // 设置过期时间
                .expiration(new Date(expMillis))
                .compact();
    }

    /**
     * 获取签名秘钥
     */
    public static byte[] getSigningKey() {
        return getJwtProperties().getSecretKey().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Token解密
     *
     * @param token 加密后的token
     */
    public static Claims parseJWT(String token) {
        // 设置签名的秘钥，解析 0.12 中由 parseClaimsJws 更名为 parseSignedClaims
        return Jwts.parser()
                .verifyWith(hmacKey(getJwtProperties().getSecretKey()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static SecretKey hmacKey(String secretKey) {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

}
