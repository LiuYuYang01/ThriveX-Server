package liuyuyang.net.web.service;

import jakarta.annotation.Resource;
import liuyuyang.net.core.storage.StorageServiceRouter;
import liuyuyang.net.web.mapper.EnvConfigMapper;
import liuyuyang.net.web.mapper.PageConfigMapper;
import liuyuyang.net.web.mapper.RecordCommentMapper;
import liuyuyang.net.model.Article;
import liuyuyang.net.model.Comment;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.model.Footprint;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.Milestone;
import liuyuyang.net.model.PageConfig;
import liuyuyang.net.model.Record;
import liuyuyang.net.model.RecordComment;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.model.User;
import liuyuyang.net.model.WebConfig;
import liuyuyang.net.vo.file.FileCleanupItemVO;
import liuyuyang.net.vo.file.FileCleanupScanVO;
import liuyuyang.net.vo.file.FileTreeFileVO;
import liuyuyang.net.vo.file.FileTreeNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 未引用文件扫描：把业务数据（文章、轮播图、配置等）中可能出现 URL 的字段原样拼接成引用文本，
 * 提取其中所有“文件名 token”构成引用集合；存储中的文件名不在集合内即为清理候选。
 * <p>
 * 上传文件名由 {@code UUID + 扩展名} 生成、全局唯一，因此无论引用以 Markdown、HTML 还是 JSON
 * 形态保存、无论存储域名如何变更，文件名都会原样出现在引用文本里，按文件名 containment 判定
 * 无需解析 URL。该方向的误判只会把“已引用”误判为“已引用”（多保护），不会误删。
 * <p>
 * 已知盲区：仅存在于前端本地草稿中的图片引用后端不可见，扫描结果需人工核对；
 * 手工放到存储里的非 UUID 命名文件（如含中文/空格）可能无法被 token 匹配保护，扫描结果中可人工勾掉。
 */
@Slf4j
@Service
public class FileCleanupService {
    // 与 LocalStorageService / QiniuStorageService 一致的目录占位文件
    private static final String PLACEHOLDER_FILE_NAME = ".keep";
    // 与 ImagePfopUtils.TMP_SUFFIX 一致的瘦身临时文件后缀
    private static final String PFOP_TMP_SUFFIX = ".thrive-pfop.tmp";

    /**
     * 文件名 token：一段不含 URL 结构符（空白、/?#"'<>()[]{}|;%,* 等）的连续字符 + 1~10 位字母数字扩展名。
     * 结构符作为边界可防止相邻两个文件名被连接成一个 token 而漏掉前者。
     */
    private static final Pattern FILE_NAME_TOKEN = Pattern.compile("[^\\s/\\?#\"'<>()\\[\\]{}`|;%,*]+\\.[A-Za-z0-9]{1,10}");

    @Resource
    private StorageServiceRouter storageServiceRouter;

    @Resource
    private ArticleService articleService;
    @Resource
    private SwiperService swiperService;
    @Resource
    private LinkService linkService;
    @Resource
    private MilestoneService milestoneService;
    @Resource
    private FootprintService footprintService;
    @Resource
    private RecordService recordService;
    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;
    @Resource
    private WebConfigService webConfigService;

    @Resource
    private RecordCommentMapper recordCommentMapper;
    @Resource
    private PageConfigMapper pageConfigMapper;
    @Resource
    private EnvConfigMapper envConfigMapper;

    /**
     * 扫描当前存储中未被任何业务数据引用的文件。
     */
    public FileCleanupScanVO scan() {
        Set<String> referencedNames = extractReferencedNames(collectReferenceText());

        List<FileCleanupItemVO> candidates = new ArrayList<>();
        collectCandidates(storageServiceRouter.service().listFileTree().getResult(), referencedNames, candidates);
        candidates.sort(Comparator.comparing(FileCleanupItemVO::getDate,
                Comparator.nullsLast(Comparator.reverseOrder())));

        FileCleanupScanVO vo = new FileCleanupScanVO();
        vo.setCandidates(candidates);
        vo.setCount(candidates.size());
        vo.setTotalSize(candidates.stream().mapToLong(item -> item.getSize() == null ? 0L : item.getSize()).sum());
        vo.setScanTime(System.currentTimeMillis());
        return vo;
    }

    /**
     * 汇总所有可能保存文件 URL 的业务字段。字段之间以换行分隔，
     * 避免前后字段拼接处意外连成一个 token。文章软删除仅标记 article_config，
     * article 行仍在表中，回收站文章引用的图片同样会被保护。
     */
    private String collectReferenceText() {
        StringBuilder sb = new StringBuilder();

        for (Article article : articleService.list()) {
            appendLine(sb, article.getContent());
            appendLine(sb, article.getCover());
        }
        for (Swiper swiper : swiperService.list()) {
            appendLine(sb, swiper.getImage());
        }
        for (Link link : linkService.list()) {
            appendLine(sb, link.getImage());
        }
        for (Milestone milestone : milestoneService.list()) {
            appendLine(sb, milestone.getImage());
        }
        for (Footprint footprint : footprintService.list()) {
            if (footprint.getImages() != null) {
                footprint.getImages().forEach(image -> appendLine(sb, image));
            }
        }
        for (Record record : recordService.list()) {
            appendLine(sb, record.getImages());
            appendLine(sb, record.getVideo());
        }
        for (Comment comment : commentService.list()) {
            appendLine(sb, comment.getAvatar());
        }
        for (RecordComment recordComment : recordCommentMapper.selectList(null)) {
            appendLine(sb, recordComment.getAvatar());
        }
        for (User user : userService.list()) {
            appendLine(sb, user.getAvatar());
        }
        // 配置表为 JSON，整体转为字符串即可，新增图片类配置字段无需改动此处
        for (WebConfig webConfig : webConfigService.list()) {
            appendLine(sb, String.valueOf(webConfig.getValue()));
        }
        for (PageConfig pageConfig : pageConfigMapper.selectList(null)) {
            appendLine(sb, String.valueOf(pageConfig.getValue()));
        }
        for (EnvConfig envConfig : envConfigMapper.selectList(null)) {
            appendLine(sb, String.valueOf(envConfig.getValue()));
        }
        return sb.toString();
    }

    private void appendLine(StringBuilder sb, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(value).append('\n');
        }
    }

    /**
     * 从引用文本中提取全部文件名 token（统一小写）。域名等杂音 token 也会进入集合，
     * 但只会让文件被多保护，不会产生误删。
     */
    private Set<String> extractReferencedNames(String text) {
        Set<String> names = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return names;
        }
        Matcher matcher = FILE_NAME_TOKEN.matcher(text);
        while (matcher.find()) {
            names.add(matcher.group().toLowerCase());
        }
        return names;
    }

    /**
     * 深度遍历文件树，收集同时满足以下条件的文件：
     * 非 {@code .keep} 占位、非七牛瘦身临时文件、文件名未出现在引用集合中。
     */
    private void collectCandidates(List<FileTreeNodeVO> nodes, Set<String> referencedNames,
                                   List<FileCleanupItemVO> candidates) {
        if (nodes == null) {
            return;
        }
        for (FileTreeNodeVO node : nodes) {
            if (node.getFiles() != null) {
                for (FileTreeFileVO file : node.getFiles()) {
                    String name = file.getName();
                    if (name == null || PLACEHOLDER_FILE_NAME.equals(name) || name.endsWith(PFOP_TMP_SUFFIX)) {
                        continue;
                    }
                    if (referencedNames.contains(name.toLowerCase())) {
                        continue;
                    }
                    FileCleanupItemVO item = new FileCleanupItemVO();
                    item.setName(name);
                    item.setDir(file.getDir());
                    item.setPath(file.getPath());
                    item.setUrl(file.getUrl());
                    item.setSize(file.getSize());
                    item.setDate(file.getDate());
                    candidates.add(item);
                }
            }
            collectCandidates(node.getChildren(), referencedNames, candidates);
        }
    }
}
