package liuyuyang.net.enums.file;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文件管理模块允许上传的视频扩展名及对应 MIME。
 */
@Getter
public enum FileVideoExtensionEnum {
    MP4("mp4", "video/mp4"),
    WEBM("webm", "video/webm"),
    OGG("ogg", "video/ogg"),
    OGV("ogv", "video/ogg"),
    MOV("mov", "video/quicktime"),
    M4V("m4v", "video/x-m4v"),
    AVI("avi", "video/x-msvideo"),
    MKV("mkv", "video/x-matroska"),
    FLV("flv", "video/x-flv");

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
