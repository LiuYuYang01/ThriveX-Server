package liuyuyang.net.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.model.ArticleConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 文章访问密码只写不读：请求可携带，任何接口响应都不得回显明文。
 * 加密文章的正文虽然由服务端打码，但密码一旦随 config 下发给匿名用户，打码就形同虚设。
 */
public class ArticleConfigJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void responseMustNotLeakPassword() throws Exception {
        ArticleConfig config = new ArticleConfig();
        config.setPassword("123456");
        config.setIsEncrypt(true);

        String json = mapper.writeValueAsString(config);

        assertFalse(json.contains("123456"), "响应中不得出现密码明文");
        assertFalse(json.contains("\"password\""), "响应中不得出现 password 字段");
        assertTrue(json.contains("\"hasPassword\":true"), "应通过 hasPassword 告知前端已设置密码");
    }

    @Test
    public void emptyPasswordReportsFalse() throws Exception {
        ArticleConfig config = new ArticleConfig();
        config.setPassword("");
        assertTrue(mapper.writeValueAsString(config).contains("\"hasPassword\":false"));

        config.setPassword(null);
        assertTrue(mapper.writeValueAsString(config).contains("\"hasPassword\":false"));
    }

    @Test
    public void requestCanStillCarryPassword() throws Exception {
        ArticleConfig config = mapper.readValue("{\"password\":\"abc\",\"isEncrypt\":true}", ArticleConfig.class);

        assertEquals("abc", config.getPassword(), "新增/编辑文章时仍需要能接收密码");
        assertTrue(config.getHasPassword());
    }
}
