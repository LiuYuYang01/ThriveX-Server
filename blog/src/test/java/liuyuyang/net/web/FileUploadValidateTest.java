package liuyuyang.net.web;

import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.web.service.impl.FileServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 文件上传格式校验的边界测试。
 * <p>
 * 直接反射调用 {@code FileServiceImpl#validateFile}，不启动 Spring 上下文，
 * 用真实的「扩展名 + 浏览器上报 Content-Type」组合验证白名单行为。
 * <p>
 * 校验规则（见 FileServiceImpl#validateFile）：扩展名必须命中图片或视频枚举，
 * MIME 只需命中「同类枚举的 MIME 并集」，两者不做交叉绑定。
 */
class FileUploadValidateTest {

    private static final Method VALIDATE;

    static {
        try {
            VALIDATE = FileServiceImpl.class.getDeclaredMethod(
                    "validateFile", org.springframework.web.multipart.MultipartFile.class);
            VALIDATE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /** 返回 null 表示放行，否则返回拒绝原因。 */
    private String probe(String filename, String contentType) {
        MockMultipartFile file = new MockMultipartFile("files", filename, contentType, new byte[]{0, 0, 0, 1});
        try {
            VALIDATE.invoke(new FileServiceImpl(), file);
            return null;
        } catch (Exception e) {
            Throwable cause = e.getCause() == null ? e : e.getCause();
            if (cause instanceof CustomException custom) {
                return custom.getMessage();
            }
            throw new IllegalStateException(cause);
        }
    }

    private void assertAccepted(String filename, String contentType) {
        assertDoesNotThrow(() -> {
            String msg = probe(filename, contentType);
            if (msg != null) {
                throw new AssertionError(filename + " / " + contentType + " 被拒：" + msg);
            }
        });
    }

    private void assertRejected(String filename, String contentType) {
        assertThrows(CustomException.class, () -> {
            String msg = probe(filename, contentType);
            if (msg == null) {
                return;
            }
            throw new CustomException(msg);
        });
    }

    @Test
    @DisplayName("九种视频扩展名 + 标准 MIME 全部放行")
    void standardVideoTypesAreAccepted() {
        assertAccepted("a.mp4", "video/mp4");
        assertAccepted("a.webm", "video/webm");
        assertAccepted("a.ogg", "video/ogg");
        assertAccepted("a.ogv", "video/ogg");
        assertAccepted("a.mov", "video/quicktime");
        assertAccepted("a.m4v", "video/x-m4v");
        assertAccepted("a.avi", "video/x-msvideo");
        assertAccepted("a.mkv", "video/x-matroska");
        assertAccepted("a.flv", "video/x-flv");
    }

    @Test
    @DisplayName("浏览器实际上报的变体 MIME：MIME 并集校验带来的兼容/风险")
    void browserReportedMimeVariants() {
        // Chrome 对 .m4v 常按 mp4 上报，靠「MIME 并集」得以放行
        assertAccepted("a.m4v", "video/mp4");
        // .ogv 在部分浏览器上报 video/ogg，同样受益于并集
        assertAccepted("a.ogv", "video/ogg");
        // Windows 注册表里的 .avi 类型不在白名单，会被误拒
        assertRejected("a.avi", "video/avi");
        // 上报不出 MIME（部分系统 .mkv/.flv）会被拒
        assertRejected("a.mkv", null);
        // 伪造 MIME 也能过：扩展名与 MIME 不交叉校验
        assertAccepted("a.avi", "video/mp4");
    }

    @Test
    @DisplayName("扩展名大小写、非白名单格式、图片仍受三层校验")
    void extensionCaseAndNonWhitelisted() {
        assertAccepted("A.MP4", "video/mp4");
        assertRejected("a.txt", "text/plain");
        assertRejected("noext", "video/mp4");
        // 视频扩展名配图片 MIME：图片 MIME 不在视频并集内，拒绝
        assertRejected("a.mp4", "image/png");
    }

    @Test
    @DisplayName("打印完整判定矩阵，便于人工核对")
    void printMatrix() {
        String[] names = {"a.mp4", "a.webm", "a.ogv", "a.mov", "a.m4v", "a.avi", "a.mkv", "a.flv", "a.txt"};
        String[] types = {"video/mp4", "video/quicktime", "video/x-m4v", "video/x-msvideo",
                "video/avi", "video/x-matroska", "application/octet-stream", null};

        Map<String, String> matrix = new LinkedHashMap<>();
        for (String name : names) {
            for (String type : types) {
                String msg = probe(name, type);
                matrix.put(name + " | " + type, msg == null ? "放行" : "拒绝");
            }
        }
        matrix.forEach((k, v) -> System.out.println("[上传判定] " + k + " => " + v));

        assertTrue(matrix.containsValue("放行"));
    }
}
