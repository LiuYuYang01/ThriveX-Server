package liuyuyang.net.core.utils;

import io.jsonwebtoken.ExpiredJwtException;
import liuyuyang.net.core.properties.JwtProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtUtilsTest {

    private static final String SECRET = "unit-test-secret-key-at-least-32-bytes!";

    private static void init() {
        JwtProperties properties = new JwtProperties();
        properties.setSecretKey(SECRET);
        properties.setTtl(259200000L);
        properties.setTokenName("Authorization");
        JwtUtils.setJwtProperties(properties);
    }

    @Test
    public void createAndParseRoundtrip() {
        init();
        String token = JwtUtils.createJWT(Map.of("username", "admin", "id", 1));
        var claims = JwtUtils.parseJWT(token);
        assertEquals("admin", claims.get("username", String.class));
        assertEquals(1, claims.get("id", Integer.class));
        assertTrue(claims.getExpiration().after(new java.util.Date()));
    }

    @Test
    public void rejectsTamperedToken() {
        init();
        String token = JwtUtils.createJWT(Map.of("username", "admin"));
        String tampered = token.substring(0, token.length() - 2) + "xx";
        assertThrows(Exception.class, () -> JwtUtils.parseJWT(tampered));
    }

    @Test
    public void rejectsExpiredToken() {
        // ttl 传负值生成一个已过期的 token
        String token = JwtUtils.createJWT(SECRET, -1000L, Map.of("username", "admin"));
        assertThrows(ExpiredJwtException.class, () -> JwtUtils.parseJWT(token));
    }
}
