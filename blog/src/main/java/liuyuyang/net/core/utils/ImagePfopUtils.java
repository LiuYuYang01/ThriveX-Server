package liuyuyang.net.core.utils;

import com.qiniu.util.UrlSafeBase64;

import java.util.Locale;

/**
 * 七牛 pfop 图片瘦身：按体积与格式生成 fops，并拼接 saveas 到临时 key。
 */
public final class ImagePfopUtils {

    private static final long MIN_SIZE_BYTES = 500L * 1024L;
    private static final double MIN_SAVE_RATIO = 0.05D;
    public static final String TMP_SUFFIX = ".thrive-pfop.tmp";

    private ImagePfopUtils() {
    }

    public static final class Plan {
        private final boolean skip;
        private final String reason;
        private final String fops;

        private Plan(boolean skip, String reason, String fops) {
            this.skip = skip;
            this.reason = reason;
            this.fops = fops;
        }

        public static Plan skip(String reason) {
            return new Plan(true, reason, null);
        }

        public static Plan process(String fops) {
            return new Plan(false, null, fops);
        }

        public boolean isSkip() {
            return skip;
        }

        public String getReason() {
            return reason;
        }

        public String getFops() {
            return fops;
        }
    }

    public static String buildTmpKey(String key) {
        return key + TMP_SUFFIX;
    }

    public static String buildPfopFops(String fops, String bucket, String tmpKey) {
        String entry = UrlSafeBase64.encodeToString(bucket + ":" + tmpKey);
        return fops + "|saveas/" + entry;
    }

    public static boolean isInsufficientSaving(long beforeSize, long afterSize) {
        if (beforeSize <= 0L) {
            return true;
        }
        return afterSize >= beforeSize * (1D - MIN_SAVE_RATIO);
    }

    public static Plan resolvePlan(String ext, long size, String mode) {
        if (size < MIN_SIZE_BYTES) {
            return Plan.skip("文件已小于 500KB，无需瘦身");
        }

        String normalizedExt = normalizeExt(ext);
        String normalizedMode = mode == null || mode.trim().isEmpty() ? "auto" : mode.trim().toLowerCase(Locale.ROOT);

        if ("webp".equals(normalizedExt)) {
            int quality = resolveWebpQuality(size, normalizedMode);
            return Plan.process("imageMogr2/quality/" + quality);
        }
        if (!"jpg".equals(normalizedExt) && !"jpeg".equals(normalizedExt) && !"png".equals(normalizedExt)) {
            return Plan.skip("不支持的图片格式，仅支持 jpg、jpeg、png、webp");
        }

        if ("png".equals(normalizedExt) && size > 5L * 1024L * 1024L) {
            return Plan.process("imageMogr2/thumbnail/1920x>/quality/75");
        }

        int zlevel = resolveZlevel(size, normalizedMode);
        return Plan.process("imageslim/zlevel/" + zlevel);
    }

    public static String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int index = filename.lastIndexOf('.');
        if (index < 0 || index >= filename.length() - 1) {
            return "";
        }
        return filename.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    public static double calcSavedPercent(long beforeSize, long afterSize) {
        if (beforeSize <= 0L) {
            return 0D;
        }
        double saved = Math.max(0D, (beforeSize - afterSize) * 100D / beforeSize);
        return Math.round(saved * 10D) / 10D;
    }

    public static String formatSavedBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024L * 1024L) {
            return String.format(Locale.ROOT, "%.2f KB", bytes / 1024D);
        }
        return String.format(Locale.ROOT, "%.2f MB", bytes / (1024D * 1024D));
    }

    private static String normalizeExt(String ext) {
        return ext == null ? "" : ext.toLowerCase(Locale.ROOT).replace(".", "");
    }

    private static int resolveZlevel(long size, String mode) {
        if ("light".equals(mode)) {
            return size > 1024L * 1024L ? 6 : 7;
        }
        if ("strong".equals(mode)) {
            return size > 5L * 1024L * 1024L ? 2 : 3;
        }
        if ("medium".equals(mode)) {
            return size > 5L * 1024L * 1024L ? 3 : 5;
        }
        if (size > 5L * 1024L * 1024L) {
            return 2;
        }
        if (size > 1024L * 1024L) {
            return 4;
        }
        return 6;
    }

    private static int resolveWebpQuality(long size, String mode) {
        if ("light".equals(mode)) {
            return 85;
        }
        if ("strong".equals(mode)) {
            return size > 2L * 1024L * 1024L ? 65 : 70;
        }
        if ("medium".equals(mode)) {
            return 75;
        }
        return size > 2L * 1024L * 1024L ? 70 : 80;
    }
}
