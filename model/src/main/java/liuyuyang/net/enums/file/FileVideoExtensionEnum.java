package liuyuyang.net.enums.file;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文件管理模块允许上传的视频扩展名及对应 MIME。
 * <p>
 * 只保留 MP4：浏览器能直接在线播放的视频格式里，只有 MP4(H.264) 做到了
 * Chrome / Firefox / Safari / iOS / Android 全平台覆盖；mov、avi、mkv、flv 等
 * 要么容器不被识别、要么内部编码（HEVC、ProRes）无法解码，传上去等于死文件。
 */
@Getter
public enum FileVideoExtensionEnum {
    MP4("mp4", "video/mp4");

    private final String extension;
    private final String mimeType;

    FileVideoExtensionEnum(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static Set<String> allowedMimeTypes() {
        return Collections.unmodifiableSet(
                Arrays.stream(values()).map(FileVideoExtensionEnum::getMimeType).collect(Collectors.toSet()));
    }

    public static Set<String> allowedExtensions() {
        return Collections.unmodifiableSet(
                Arrays.stream(values()).map(FileVideoExtensionEnum::getExtension).collect(Collectors.toSet()));
    }
}
