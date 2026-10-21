package liuyuyang.net.web;

import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.web.service.impl.FileServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 文件上传格式校验的边界测试。
 * <p>
 * 直接反射调用 {@code FileServiceImpl#validateFile}，不启动 Spring 上下文，
 * 用真实的「扩展名 + 浏览器上报 Content-Type」组合验证白名单行为。
 * <p>
 * 校验规则（见 FileServiceImpl#validateFile）：视频只允许 mp4（对应 MIME video/mp4），
 * 图片允许 jpg/jpeg/png/webp；MIME 必须与扩展名所属类别匹配，不做跨类别放行；
 * 图片还要通过 ImageIO 解码校验。
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
        return probe(filename, contentType, new byte[]{0, 0, 0, 1});
    }

    private String probe(String filename, String contentType, byte[] content) {
        MockMultipartFile file = new MockMultipartFile("files", filename, contentType, content);
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

    /** 生成一张真实可解码的小图，供图片的 ImageIO 解码校验使用。 */
    private byte[] imageBytes(String format) {
        try {
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, format, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
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
        assertRejected(filename, contentType, new byte[]{0, 0, 0, 1});
    }

    private void assertRejected(String filename, String contentType, byte[] content) {
        assertThrows(CustomException.class, () -> {
            String msg = probe(filename, contentType, content);
            if (msg == null) {
                return;
            }
            throw new CustomException(msg);
        });
    }

    @Test
    @DisplayName("视频只允许 mp4，且扩展名大小写不敏感")
    void onlyMp4VideoIsAccepted() {
        assertAccepted("a.mp4", "video/mp4");
        assertAccepted("A.MP4", "video/mp4");
        assertAccepted("movie.mp4", "video/mp4");
    }

    @Test
    @DisplayName("其余视频格式一律拒绝，无论浏览器上报什么 MIME")
    void otherVideoFormatsAreRejected() {
        assertRejected("a.mov", "video/quicktime");
        assertRejected("a.webm", "video/webm");
        assertRejected("a.ogg", "video/ogg");
        assertRejected("a.ogv", "video/ogg");
        assertRejected("a.m4v", "video/x-m4v");
        assertRejected("a.avi", "video/x-msvideo");
        assertRejected("a.mkv", "video/x-matroska");
        assertRejected("a.flv", "video/x-flv");
        // 改成 .mp4 后缀依然会被 MIME 校验拦下（浏览器仍上报原格式 MIME）
        assertRejected("a.mov", "video/mp4");
        assertRejected("a.mkv", "video/mp4");
    }

    @Test
    @DisplayName("MIME 必须与扩展名匹配：mp4 只认 video/mp4")
    void mimeMustMatchExtension() {
        assertRejected("a.mp4", "video/quicktime");
        assertRejected("a.mp4", "application/octet-stream");
        assertRejected("a.mp4", null);
        assertRejected("a.mp4", "image/png");
        assertRejected("a.jpg", "video/mp4");
        assertRejected("a.txt", "text/plain");
        assertRejected("noext", "video/mp4");
    }

    @Test
    @DisplayName("图片白名单保持不变，jpg/png 通过内容解码校验")
    void imagesStillWork() throws IOException {
        assertDoesNotThrow(() -> {
            String msg = probe("a.jpg", "image/jpeg", imageBytes("jpg"));
            if (msg != null) {
                throw new AssertionError("a.jpg 被拒：" + msg);
            }
        });
        assertDoesNotThrow(() -> {
            String msg = probe("a.png", "image/png", imageBytes("png"));
            if (msg != null) {
                throw new AssertionError("a.png 被拒：" + msg);
            }
        });
        // 扩展名与 MIME 对得上，但内容不是图片，仍应被拦下
        assertRejected("a.png", "image/png");
    }

    @Test
    @DisplayName("webp 已进入白名单，但 JDK 的 ImageIO 没有 WebP 解码器，只能卡在内容校验上")
    void webpPassesWhitelistButFailsContentCheck() {
        String msg = probe("a.webp", "image/webp");
        assertEquals("文件内容不是有效的图片", msg);
    }

    @Test
    @DisplayName("打印完整判定矩阵，便于人工核对")
    void printMatrix() {
        String[] names = {"a.mp4", "a.mov", "a.webm", "a.avi", "a.mkv", "a.jpg", "a.png", "a.gif", "a.txt"};
        String[] types = {"video/mp4", "video/quicktime", "video/webm", "image/jpeg", "image/png",
                "image/gif", "application/octet-stream", null};

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
