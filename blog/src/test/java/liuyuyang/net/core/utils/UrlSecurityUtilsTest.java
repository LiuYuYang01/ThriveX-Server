package liuyuyang.net.core.utils;

import liuyuyang.net.core.execption.CustomException;
import org.junit.Test;

public class UrlSecurityUtilsTest {

    @Test(expected = CustomException.class)
    public void rejectsNat64Loopback() {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[64:ff9b::7f00:1]/rss.xml");
    }

    @Test(expected = CustomException.class)
    public void rejects6to4PrivateNetwork() {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[2002:c0a8:0101::]/rss.xml");
    }

    @Test(expected = CustomException.class)
    public void rejectsTeredoPrivateNetwork() {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[2001:0000::3f57:ffd2]/rss.xml");
    }

    @Test(expected = CustomException.class)
    public void rejectsIpv4CompatiblePrivateNetwork() {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://[::10.0.0.1]/rss.xml");
    }

    @Test(expected = CustomException.class)
    public void rejectsPlainLoopback() {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://127.0.0.1/rss.xml");
    }

    @Test
    public void allowsPublicIpv4() {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "http://8.8.8.8/rss.xml");
    }

    @Test
    public void allowsEmptyUrl() {
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", "");
        UrlSecurityUtils.validateExternalHttpUrl("RSS 地址", null);
    }
}
