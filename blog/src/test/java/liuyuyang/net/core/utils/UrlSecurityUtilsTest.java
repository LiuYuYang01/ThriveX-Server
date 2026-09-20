package liuyuyang.net.core.utils;

import liuyuyang.net.core.execption.CustomException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlSecurityUtilsTest {

    @Test
    public void rejectsNat64Loopback() {
        assertThrows(CustomException.class, () -> UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[64:ff9b::7f00:1]/rss.xml"));
    }

    @Test
    public void rejects6to4PrivateNetwork() {
        assertThrows(CustomException.class, () -> UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[2002:c0a8:0101::]/rss.xml"));
    }

    @Test
    public void rejectsTeredoPrivateNetwork() {
        assertThrows(CustomException.class, () -> UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[2001:0000::3f57:ffd2]/rss.xml"));
    }

    @Test
    public void rejectsIpv4CompatiblePrivateNetwork() {
        assertThrows(CustomException.class, () -> UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[::10.0.0.1]/rss.xml"));
    }

    @Test
    public void rejectsPlainLoopback() {
        assertThrows(CustomException.class, () -> UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://127.0.0.1/rss.xml"));
    }

    @Test
    public void allowsPublicIpv4() {
        assertDoesNotThrow(() -> UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://8.8.8.8/rss.xml"));
    }

    @Test
    public void allowsEmptyUrl() {
        assertDoesNotThrow(() -> {
            UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "");
            UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", null);
        });
    }
}
