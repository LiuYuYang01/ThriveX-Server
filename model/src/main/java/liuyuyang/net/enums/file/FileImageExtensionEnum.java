package liuyuyang.net.enums.file;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文件管理模块允许上传的图片扩展名及对应 MIME。
 */
@Getter
public enum FileImageExtensionEnum {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp");

    private final String extension;
    private final String mimeType;

    FileImageExtensionEnum(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static Set<String> allowedMimeTypes() {
        return Collections.unmodifiableSet(
                Arrays.stream(values()).map(FileImageExtensionEnum::getMimeType).collect(Collectors.toSet()));
    }

    public static Set<String> allowedExtensions() {
        return Collections.unmodifiableSet(
                Arrays.stream(values()).map(FileImageExtensionEnum::getExtension).collect(Collectors.toSet()));
    }
}
