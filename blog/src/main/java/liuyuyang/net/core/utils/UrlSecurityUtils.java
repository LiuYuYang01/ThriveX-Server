package liuyuyang.net.core.utils;

import liuyuyang.net.core.execption.CustomException;
import org.springframework.util.StringUtils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.Locale;

/**
 * SSRF 防护工具（URL 白名单校验）
 *
 * 设计目标：
 * - 在服务端发起外部请求前，验证目标 URL 是否“公网且安全”。
 * - 统一拦截用户可控 URL，避免将服务器当作内网探测器使用。
 *
 * 校验规则：
 * 1) 仅允许 http/https 协议（拒绝 file/gopher/ftp 等协议）。
 * 2) host 必须存在，且禁止 localhost。
 * 3) 对 host 做 DNS 解析，任一解析结果命中内网/本地地址则整体拒绝。
 * 4) 同时覆盖 IPv4 与 IPv6 的回环、私网、链路本地，以及 IPv6 过渡地址（NAT64、6to4、Teredo、IPv4-compatible）内嵌的 IPv4。
 *
 * 推荐用法：
 * - 写入时校验：阻止恶意 URL 入库（第一道防线）。
 * - 请求前再校验：防历史脏数据/绕过场景（第二道防线）。
 */

/**
 * URL 安全校验工具：限制协议并阻止访问内网/本机地址，防止 SSRF。
 */
public final class UrlSecurityUtils {
    private UrlSecurityUtils() {
    }

    public static void validateExternalHttpUrl(String fieldName, String rawUrl) {
        if (!StringUtils.hasText(rawUrl)) {
            return;
        }

        URI uri = parseUri(rawUrl, fieldName);
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new CustomException(fieldName + " 仅支持 http/https 协议");
        }

        String host = uri.getHost();
        if (!StringUtils.hasText(host)) {
            throw new CustomException(fieldName + " 地址非法");
        }

        String lowerHost = host.toLowerCase(Locale.ROOT);
        if ("localhost".equals(lowerHost)) {
            throw new CustomException(fieldName + " 不允许使用本地域名");
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (isInternalAddress(address)) {
                    throw new CustomException(fieldName + " 不允许使用内网或本地地址");
                }
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(fieldName + " 域名解析失败");
        }
    }

    private static URI parseUri(String rawUrl, String fieldName) {
        try {
            return URI.create(rawUrl.trim());
        } catch (Exception e) {
            throw new CustomException(fieldName + " 地址格式错误");
        }
    }

    private static boolean isInternalAddress(InetAddress address) {
        if (address == null) {
            return true;
        }

        if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return true;
        }

        if (address instanceof Inet4Address) {
            byte[] bytes = address.getAddress();
            int first = bytes[0] & 0xFF;
            int second = bytes[1] & 0xFF;
            return first == 127
                    || first == 10
                    || (first == 172 && second >= 16 && second <= 31)
                    || (first == 192 && second == 168)
                    || (first == 169 && second == 254);
        }

        if (address instanceof Inet6Address) {
            if (address.isLoopbackAddress()
                    || address.isSiteLocalAddress()
                    || address.isLinkLocalAddress()
                    || address.isAnyLocalAddress()
                    || address.getHostAddress().startsWith("fc")
                    || address.getHostAddress().startsWith("fd")) {
                return true;
            }

            InetAddress embeddedIpv4 = extractEmbeddedIpv4FromTransition(address.getAddress());
            if (embeddedIpv4 != null) {
                return isInternalAddress(embeddedIpv4);
            }
        }

        return false;
    }

    private static InetAddress extractEmbeddedIpv4FromTransition(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            return null;
        }

        try {
            if (bytes[0] == 0x00 && bytes[1] == 0x64 && (bytes[2] & 0xFF) == 0xFF && (bytes[3] & 0xFF) == 0x9B) {
                return InetAddress.getByAddress(new byte[]{bytes[12], bytes[13], bytes[14], bytes[15]});
            }

            if ((bytes[0] & 0xFF) == 0x20 && bytes[1] == 0x02) {
                return InetAddress.getByAddress(new byte[]{bytes[2], bytes[3], bytes[4], bytes[5]});
            }

            if ((bytes[0] & 0xFF) == 0x20 && bytes[1] == 0x01 && bytes[2] == 0x00 && bytes[3] == 0x00) {
                byte[] ipv4 = new byte[4];
                for (int i = 0; i < 4; i++) {
                    ipv4[i] = (byte) (~bytes[12 + i] & 0xFF);
                }
                return InetAddress.getByAddress(ipv4);
            }

            if (isIpv4CompatibleAddress(bytes)) {
                return InetAddress.getByAddress(new byte[]{bytes[12], bytes[13], bytes[14], bytes[15]});
            }
        } catch (Exception ignored) {
            return null;
        }

        return null;
    }

    private static boolean isIpv4CompatibleAddress(byte[] bytes) {
        for (int i = 0; i < 12; i++) {
            if (bytes[i] != 0) {
                return false;
            }
        }
        return true;
    }
}
