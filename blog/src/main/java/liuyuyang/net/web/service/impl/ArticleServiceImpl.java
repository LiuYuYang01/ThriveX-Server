package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.enums.article.ArticleStatusEnum;
import liuyuyang.net.core.config.CacheConfig;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.core.utils.IpUtils;
import liuyuyang.net.dto.article.ArticleFormDTO;
import liuyuyang.net.model.*;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.article.ArticleFilterDTO;
import liuyuyang.net.vo.article.ArticleVO;
import liuyuyang.net.vo.cate.CateVO;
import liuyuyang.net.web.mapper.*;
import liuyuyang.net.web.service.ArticleCateService;
import liuyuyang.net.web.service.ArticleService;
import liuyuyang.net.web.service.ArticleTagService;
import liuyuyang.net.web.service.CateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Collection;

@Service
@Transactional
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Resource
    private ArticleTagService articleTagService;
    @Resource
    private ArticleCateMapper articleCateMapper;
    @Resource
    private ArticleCateService articleCateService;
    @Resource
    private ArticleConfigMapper articleConfigMapper;
    @Resource
    private ArticleViewLogMapper articleViewLogMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private CateMapper cateMapper;
    @Resource
    private CateService cateService;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private CommonUtils commonUtils;

    @NotNull
    private static LambdaQueryWrapper<Article> getArticleQueryWrapper(ArticleFilterDTO articleFilterDTO) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getCreateTime);

        // 根据关键字通过标题过滤出对应文章数据
        if (articleFilterDTO.getTitle() != null && !articleFilterDTO.getTitle().isEmpty()) {
            queryWrapper.like(Article::getTitle, "%" + articleFilterDTO.getTitle() + "%");
        }

        // 根据开始与结束时间过滤
        if (articleFilterDTO.getStartDate() != null && articleFilterDTO.getEndDate() != null) {
            queryWrapper.between(Article::getCreateTime, articleFilterDTO.getStartDate(), articleFilterDTO.getEndDate());
        } else if (articleFilterDTO.getStartDate() != null) {
            queryWrapper.ge(Article::getCreateTime, articleFilterDTO.getStartDate());
        } else if (articleFilterDTO.getEndDate() != null) {
            queryWrapper.le(Article::getCreateTime, articleFilterDTO.getEndDate());
        }
        return queryWrapper;
    }

    @Override
    @CacheEvict(cacheNames = {CacheConfig.CACHE_SITE_SUMMARY, CacheConfig.CACHE_TAG_LIST, CacheConfig.CACHE_HOT_ARTICLE}, allEntries = true)
    public void addArticleData(ArticleFormDTO articleFormDTO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleFormDTO, article);
        articleMapper.insert(article);

        // 新增分类
        List<Integer> cateIdList = articleFormDTO.getCateIds();
        if (!cateIdList.isEmpty()) {
            ArrayList<ArticleCate> cateArrayList = new ArrayList<>(cateIdList.size());
            for (Integer id : cateIdList) {
                ArticleCate articleCate = new ArticleCate();
                articleCate.setArticleId(article.getId());
                articleCate.setCateId(id);
                cateArrayList.add(articleCate);
            }
            articleCateService.saveBatch(cateArrayList);
        }

        // 新增标签
        List<Integer> tagIdList = articleFormDTO.getTagIds();

        if (tagIdList != null && !tagIdList.isEmpty()) {
            ArrayList<ArticleTag> tagArrayList = new ArrayList<>(tagIdList.size());
            for (Integer id : tagIdList) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(id);
                tagArrayList.add(articleTag);
            }
            articleTagService.saveBatch(tagArrayList);
        }

        // 新增文章配置
        ArticleConfig config = articleFormDTO.getConfig();
        ArticleConfig articleConfig = new ArticleConfig();
        articleConfig.setArticleId(article.getId());
        articleConfig.setStatus(config.getStatus() != null ? config.getStatus() : ArticleStatusEnum.DEFAULT);
        // 统一存空串而非 null，避免依赖 password = '' 的查询（如随机推荐）漏掉数据
        articleConfig.setPassword(config.getPassword() == null ? "" : config.getPassword());
        articleConfig.setIsDraft(config.getIsDraft());
        articleConfig.setIsEncrypt(config.getIsEncrypt());
        articleConfig.setIsDel(false);

        articleConfigMapper.insert(articleConfig);
    }

    @Override
    @CacheEvict(cacheNames = {CacheConfig.CACHE_SITE_SUMMARY, CacheConfig.CACHE_TAG_LIST, CacheConfig.CACHE_HOT_ARTICLE}, allEntries = true)
    public void delArticleData(Integer id, Integer is_del) {
        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getArticleId, id);
        ArticleConfig articleConfig = articleConfigMapper.selectOne(articleConfigLambdaQueryWrapper);

        // 严格删除：直接从数据库删除
        if (is_del == 0) {
            // 删除文章关联的数据
            delArticleCorrelationData(id);

            // 删除当前文章
            articleMapper.deleteById(id);
        }

        // 普通删除：更改 is_del 字段，到时候可以通过更改字段恢复
        if (is_del == 1) {
            articleConfig.setIsDel(true);
            articleConfigMapper.updateById(articleConfig);
        }

        if (is_del != 0 && is_del != 1) {
            throw new CustomException("参数有误：请选择是否严格删除");
        }
    }

    @Override
    @CacheEvict(cacheNames = {CacheConfig.CACHE_SITE_SUMMARY, CacheConfig.CACHE_TAG_LIST, CacheConfig.CACHE_HOT_ARTICLE}, allEntries = true)
    public void recoveryArticleData(Integer id) {
        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getArticleId, id);
        ArticleConfig articleConfig = articleConfigMapper.selectOne(articleConfigLambdaQueryWrapper);
        articleConfig.setIsDel(false);
        articleConfigMapper.updateById(articleConfig);
    }

    @Override
    @CacheEvict(cacheNames = {CacheConfig.CACHE_SITE_SUMMARY, CacheConfig.CACHE_TAG_LIST, CacheConfig.CACHE_HOT_ARTICLE}, allEntries = true)
    public void delBatchArticleData(List<Integer> ids) {
        // 批量删除文章
        if (ids == null || ids.isEmpty())
            return;

        // 与单条普通删除一致：只标记 is_del，保留分类、标签等关联数据，以便从回收站还原
        LambdaUpdateWrapper<ArticleConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ArticleConfig::getArticleId, ids);
        updateWrapper.set(ArticleConfig::getIsDel, true);
        articleConfigMapper.update(null, updateWrapper);
    }

    @Override
    @CacheEvict(cacheNames = {CacheConfig.CACHE_SITE_SUMMARY, CacheConfig.CACHE_TAG_LIST, CacheConfig.CACHE_HOT_ARTICLE}, allEntries = true)
    public void editArticleData(ArticleFormDTO articleFormDTO) {
        if (articleFormDTO.getCateIds() == null || articleFormDTO.getCateIds().isEmpty())
            throw new CustomException("编辑失败：请绑定分类");

        // 密码明文不会下发给前端，因此编辑时密码留空表示沿用原密码，避免保存一次就被清空
        String originalPassword = "";
        LambdaQueryWrapper<ArticleConfig> oldConfigQuery = new LambdaQueryWrapper<>();
        oldConfigQuery.eq(ArticleConfig::getArticleId, articleFormDTO.getId());
        ArticleConfig oldConfig = articleConfigMapper.selectOne(oldConfigQuery);
        if (oldConfig != null && oldConfig.getPassword() != null) {
            originalPassword = oldConfig.getPassword();
        }

        // 删除文章关联的数据
        delArticleCorrelationData(articleFormDTO.getId());
        // 重新绑定分类
        if (articleFormDTO.getCateIds() != null && !articleFormDTO.getCateIds().isEmpty()) {
            for (Integer id : articleFormDTO.getCateIds()) {
                ArticleCate articleCate = new ArticleCate();
                articleCate.setArticleId(articleFormDTO.getId());
                articleCate.setCateId(id);
                articleCateMapper.insert(articleCate);
            }
        }

        // 重新绑定标签
        if (articleFormDTO.getTagIds() != null && !articleFormDTO.getTagIds().isEmpty()) {
            for (Integer id : articleFormDTO.getTagIds()) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleFormDTO.getId());
                articleTag.setTagId(id);
                articleTagMapper.insert(articleTag);
            }
        }

        // 重新绑定文章配置
        ArticleConfig config = articleFormDTO.getConfig();
        ArticleConfig articleConfig = new ArticleConfig();
        articleConfig.setArticleId(articleFormDTO.getId());
        articleConfig.setStatus(config.getStatus() != null ? config.getStatus() : ArticleStatusEnum.DEFAULT);
        String newPassword = config.getPassword();
        articleConfig.setPassword(newPassword == null || newPassword.trim().isEmpty() ? originalPassword : newPassword);
        articleConfig.setIsDraft(Boolean.TRUE.equals(config.getIsDraft()));
        articleConfig.setIsEncrypt(Boolean.TRUE.equals(config.getIsEncrypt()));
        articleConfig.setIsDel(false);
        articleConfigMapper.insert(articleConfig);

        Article article = new Article();
        BeanUtils.copyProperties(articleFormDTO, article);

        // 修改文章
        articleMapper.updateById(article);
    }

    @Override
    public void topArticleData(Integer id, Boolean isTop) {
        Article article = articleMapper.selectById(id);
        if (article == null)
            throw new CustomException("该文章不存在");

        Article update = new Article();
        update.setId(id);
        update.setIsTop(isTop);
        articleMapper.updateById(update);
    }

    @Override
    public ArticleVO getArticleData(Integer id, String password) {
        ArticleVO data = bindingArticleData(id);

        String description = data.getDescription();
        String content = data.getContent();

        boolean isAdmin = CommonUtils.isAdmin();

        ArticleConfig config = data.getConfig();

        if (!Boolean.TRUE.equals(data.getConfig().getIsEncrypt()) && !password.isEmpty()) {
            throw new CustomException(610, "该文章不需要访问密码");
        }

        // 普通用户对特定的文章无权查看
        if (!isAdmin) {
            if (Boolean.TRUE.equals(data.getConfig().getIsDel())) {
                throw new CustomException(404, "该文章已被删除");
            }

            if (ArticleStatusEnum.HIDE.equals(config.getStatus())) {
                throw new CustomException(611, "该文章已被隐藏");
            }

            // 如果有密码就必须通过密码才能查看
            if (Boolean.TRUE.equals(data.getConfig().getIsEncrypt())) {
                // 如果需要访问密码且没有传递密码参数
                if (password.isEmpty()) {
                    throw new CustomException(612, "请输入文章访问密码");
                }

                data.setDescription("该文章需要密码才能查看");
                data.setContent("该文章需要密码才能查看");

                // 验证密码是否正确（历史数据密码可能为 null，避免 NPE）
                String articlePassword = config.getPassword();
                if (articlePassword != null && articlePassword.equals(password)) {
                    data.setDescription(description);
                    data.setContent(content);
                } else {
                    throw new CustomException(613, "文章访问密码错误");
                }
            }
        }

        // 获取当前文章的创建时间
        Long createTime = data.getCreateTime();

        // 查询上一篇文章（只查 id 和 title）
        LambdaQueryWrapper<Article> prevQueryWrapper = new LambdaQueryWrapper<>();
        prevQueryWrapper.select(Article::getId, Article::getTitle)
                .lt(Article::getCreateTime, createTime).orderByDesc(Article::getCreateTime).last("LIMIT 1");
        Article prevArticle = articleMapper.selectOne(prevQueryWrapper);

        if (prevArticle != null) {
            // 检查文章配置
            LambdaQueryWrapper<ArticleConfig> prevConfigWrapper = new LambdaQueryWrapper<>();
            prevConfigWrapper.eq(ArticleConfig::getArticleId, prevArticle.getId()).eq(ArticleConfig::getIsDel, false);
            ArticleConfig prevConfig = articleConfigMapper.selectOne(prevConfigWrapper);

            if (prevConfig != null) {
                Map<String, Object> resultPrev = new HashMap<>();
                resultPrev.put("id", prevArticle.getId());
                resultPrev.put("title", prevArticle.getTitle());
                data.setPrev(resultPrev);
            }
        }

        // 查询下一篇文章（只查 id/title，避免拉取 content 大字段）
        LambdaQueryWrapper<Article> nextQueryWrapper = new LambdaQueryWrapper<>();
        nextQueryWrapper.select(Article::getId, Article::getTitle)
                .gt(Article::getCreateTime, createTime).orderByAsc(Article::getCreateTime).last("LIMIT 1");
        Article nextArticle = articleMapper.selectOne(nextQueryWrapper);

        if (nextArticle != null) {
            // 检查文章配置
            LambdaQueryWrapper<ArticleConfig> nextConfigWrapper = new LambdaQueryWrapper<>();
            nextConfigWrapper.eq(ArticleConfig::getArticleId, nextArticle.getId()).eq(ArticleConfig::getIsDel, false);
            ArticleConfig nextConfig = articleConfigMapper.selectOne(nextConfigWrapper);

            if (nextConfig != null) {
                Map<String, Object> resultNext = new HashMap<>();
                resultNext.put("id", nextArticle.getId());
                resultNext.put("title", nextArticle.getTitle());
                data.setNext(resultNext);
            }
        }

        return data;
    }

    // 处理文章数据
    @Override
    public List<ArticleVO> processArticleData(ArticleFilterDTO articleFilterDTO) {
        // 首先根据文章配置表的条件筛选出符合条件的文章ID
        LambdaQueryWrapper<ArticleConfig> configQueryWrapper = new LambdaQueryWrapper<>();

        // 根据草稿状态筛选
        if (articleFilterDTO.getIsDraft() != null) {
            configQueryWrapper.eq(ArticleConfig::getIsDraft, articleFilterDTO.getIsDraft());
        }

        // 根据删除状态筛选
        if (articleFilterDTO.getIsDel() != null) {
            configQueryWrapper.eq(ArticleConfig::getIsDel, articleFilterDTO.getIsDel());
        }

        // 获取符合条件的文章ID列表
        List<Integer> articleIds = articleConfigMapper.selectList(configQueryWrapper)
                .stream()
                .map(ArticleConfig::getArticleId)
                .collect(Collectors.toList());

        // 如果没有找到符合条件的文章ID，直接返回空列表
        if (articleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建文章查询条件
        LambdaQueryWrapper<Article> queryWrapper = queryWrapperArticle(articleFilterDTO);
        queryWrapper.in(Article::getId, articleIds);
        List<Article> list = articleMapper.selectList(queryWrapper);

        boolean isAdmin = CommonUtils.isAdmin();

        // 绑定数据 + 统一的展示规则（过滤隐藏文章 + 处理加密文章）
        return list.stream()
                .map(article -> bindingArticleData(article.getId()))
                .filter(article -> shouldShowInList(article, isAdmin))
                .map(this::maskIfEncrypted)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ArticleVO> getArticleList(ArticleFilterDTO articleFilterDTO) {
        // 1. 根据文章配置表筛选出符合草稿/删除状态的文章ID
        LambdaQueryWrapper<ArticleConfig> configQueryWrapper = new LambdaQueryWrapper<>();
        if (articleFilterDTO.getIsDraft() != null) {
            configQueryWrapper.eq(ArticleConfig::getIsDraft, articleFilterDTO.getIsDraft());
        }
        if (articleFilterDTO.getIsDel() != null) {
            configQueryWrapper.eq(ArticleConfig::getIsDel, articleFilterDTO.getIsDel());
        }
        List<Integer> articleIds = articleConfigMapper.selectList(configQueryWrapper)
                .stream()
                .map(ArticleConfig::getArticleId)
                .distinct()
                .collect(Collectors.toList());
        if (articleIds.isEmpty()) {
            return emptyArticlePage(articleFilterDTO);
        }

        // 2. 叠加标题/日期/分类/标签过滤条件，得到候选文章ID
        LambdaQueryWrapper<Article> queryWrapper = queryWrapperArticle(articleFilterDTO);
        queryWrapper.select(Article::getId);
        queryWrapper.in(Article::getId, articleIds);
        List<Integer> candidateIds = articleMapper.selectList(queryWrapper).stream()
                .map(Article::getId)
                .collect(Collectors.toList());
        if (candidateIds.isEmpty()) {
            return emptyArticlePage(articleFilterDTO);
        }

        // 3. 数据库分页查询文章（排除 content 大字段）
        Page<Article> page = CommonUtils.buildPage(articleFilterDTO);
        Page<ArticleVO> result;
        List<Article> pageArticles;
        if (page == null) {
            List<Article> all = articleMapper.selectBatchWithoutContent(candidateIds);
            result = new Page<>(1, Math.max(1, all.size()));
            result.setTotal(all.size());
            pageArticles = all;
        } else {
            Page<Article> entityPage = articleMapper.selectArticlePage(page, candidateIds);
            pageArticles = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }

        // 4. 按当页文章ID批量绑定分类/标签/配置/评论数
        List<ArticleVO> vos = batchBindingArticleData(pageArticles);

        // 5. 应用展示规则（管理员可见所有，普通用户过滤隐藏/首页隐藏 + 加密打码）
        boolean isAdmin = CommonUtils.isAdmin();
        List<ArticleVO> filtered = new ArrayList<>();
        for (ArticleVO vo : vos) {
            if (!shouldShowInList(vo, isAdmin)) {
                continue;
            }
            if (!isAdmin && !shouldShowOnHomeForNonAdmin(vo)) {
                continue;
            }
            filtered.add(maskIfEncrypted(vo));
        }

        result.setRecords(filtered);
        return result;
    }

    // 构造空分页结果
    private Page<ArticleVO> emptyArticlePage(PageDTO pageDTO) {
        Page<ArticleVO> result;
        Page<Article> page = CommonUtils.buildPage(pageDTO);
        if (page == null) {
            result = new Page<>(1, 0);
        } else {
            result = new Page<>(page.getCurrent(), page.getSize());
        }
        result.setTotal(0);
        result.setRecords(new ArrayList<>());
        return result;
    }

    /**
     * 按文章实体列表批量绑定分类/标签/配置/评论数，替代逐条 bindingArticleData 的 N+1 查询。
     * 列表页专用：不读取 content 大字段。
     */
    private List<ArticleVO> batchBindingArticleData(List<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> ids = articles.stream().map(Article::getId).collect(Collectors.toList());

        // 批量查配置
        Map<Integer, ArticleConfig> configMap = articleConfigMapper.selectList(
                        new LambdaQueryWrapper<ArticleConfig>().in(ArticleConfig::getArticleId, ids))
                .stream()
                .collect(Collectors.toMap(ArticleConfig::getArticleId, c -> c, (a, b) -> a));

        // 批量查分类关联
        Map<Integer, List<Integer>> cateIdMap = new HashMap<>();
        List<ArticleCate> cateRelations = articleCateMapper.selectList(
                new LambdaQueryWrapper<ArticleCate>().in(ArticleCate::getArticleId, ids));
        Set<Integer> allCateIds = new HashSet<>();
        for (ArticleCate ac : cateRelations) {
            cateIdMap.computeIfAbsent(ac.getArticleId(), k -> new ArrayList<>()).add(ac.getCateId());
            allCateIds.add(ac.getCateId());
        }
        Map<Integer, Cate> cateMap = new HashMap<>();
        if (!allCateIds.isEmpty()) {
            cateMapper.selectByIds(allCateIds).forEach(c -> cateMap.put(c.getId(), c));
        }

        // 批量查标签关联
        Map<Integer, List<Integer>> tagIdMap = new HashMap<>();
        List<ArticleTag> tagRelations = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, ids));
        Set<Integer> allTagIds = new HashSet<>();
        for (ArticleTag at : tagRelations) {
            tagIdMap.computeIfAbsent(at.getArticleId(), k -> new ArrayList<>()).add(at.getTagId());
            allTagIds.add(at.getTagId());
        }
        Map<Integer, Tag> tagMap = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            tagMapper.selectByIds(allTagIds).forEach(t -> tagMap.put(t.getId(), t));
        }

        // 批量查评论数
        Map<Integer, Integer> commentCountMap = countCommentsByArticleIds(ids);

        // 组装 VO
        List<ArticleVO> vos = new ArrayList<>(articles.size());
        for (Article article : articles) {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);

            // 配置
            ArticleConfig config = configMap.get(article.getId());
            vo.setConfig(config);

            // 分类
            List<Integer> cateIds = cateIdMap.get(article.getId());
            if (cateIds != null) {
                List<CateVO> cates = cateIds.stream()
                        .map(cateMap::get)
                        .filter(Objects::nonNull)
                        .map(cate -> {
                            CateVO cateVO = new CateVO();
                            BeanUtils.copyProperties(cate, cateVO);
                            return cateVO;
                        })
                        .collect(Collectors.toList());
                vo.setCateList(cates);
            }

            // 标签
            List<Integer> tagIds = tagIdMap.get(article.getId());
            if (tagIds != null) {
                List<Tag> tags = tagIds.stream()
                        .map(tagMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                vo.setTagList(tags);
            }

            // 评论数
            vo.setComment(commentCountMap.getOrDefault(article.getId(), 0));

            vos.add(vo);
        }
        return vos;
    }

    // 批量统计文章评论数（替代 bindingArticleData 里查全量评论只为取 size）
    private Map<Integer, Integer> countCommentsByArticleIds(List<Integer> articleIds) {
        if (articleIds.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("article_id AS articleId", "COUNT(*) AS cnt")
                .in("article_id", articleIds)
                .groupBy("article_id");
        Map<Integer, Integer> result = new HashMap<>();
        for (Map<String, Object> row : commentMapper.selectMaps(queryWrapper)) {
            result.put(((Number) row.get("articleId")).intValue(), ((Number) row.get("cnt")).intValue());
        }
        return result;
    }

    // 获取指定分类中所有文章
    @Override
    public Page<ArticleVO> getCateArticleList(Integer id, PageDTO pageDTO) {
        Cate cate = cateMapper.selectById(id);
        if (cate == null || (!CommonUtils.isAdmin() && Boolean.TRUE.equals(cate.getIsHide()))) {
            throw new CustomException("该分类不存在");
        }

        // 通过分类 id 查询出所有文章id
        LambdaQueryWrapper<ArticleCate> queryWrapperArticleCate = new LambdaQueryWrapper<>();
        queryWrapperArticleCate.eq(ArticleCate::getCateId, id); // 修改in为eq,因为只查询单个分类
        List<Integer> articleIds = articleCateMapper.selectList(queryWrapperArticleCate).stream()
                .map(ArticleCate::getArticleId)
                .collect(Collectors.toList());

        // 有数据就查询，没有就返回空数组
        if (articleIds.isEmpty()) {
            return emptyArticlePage(pageDTO);
        }

        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.in(ArticleConfig::getArticleId, articleIds);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDraft, false);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDel, false);
        articleIds = articleConfigMapper.selectList(articleConfigLambdaQueryWrapper).stream()
                .map(ArticleConfig::getArticleId).collect(Collectors.toList());

        // 去重避免 IN 条件膨胀（理论上可能出现重复 article_id）
        articleIds = articleIds.stream().distinct().collect(Collectors.toList());

        // 如果过滤后没有文章,直接返回空页
        if (articleIds.isEmpty()) {
            return emptyArticlePage(pageDTO);
        }

        // 数据库分页查询文章（排除 content 大字段）
        Page<Article> page = CommonUtils.buildPage(pageDTO);
        Page<ArticleVO> result;
        List<Article> pageArticles;
        if (page == null) {
            List<Article> all = articleMapper.selectBatchWithoutContent(articleIds);
            result = new Page<>(1, Math.max(1, all.size()));
            result.setTotal(all.size());
            pageArticles = all;
        } else {
            Page<Article> entityPage = articleMapper.selectArticlePage(page, articleIds);
            pageArticles = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }

        // 批量绑定 + 普通用户展示规则（分类页过滤隐藏 + 加密打码）
        List<ArticleVO> vos = batchBindingArticleData(pageArticles);
        List<ArticleVO> filtered = vos.stream()
                .filter(vo -> shouldShowInList(vo, false))
                .map(this::maskIfEncrypted)
                .collect(Collectors.toList());

        result.setRecords(filtered);
        return result;
    }

    @Override
    public Page<ArticleVO> getTagArticleList(Integer id, PageDTO pageDTO) {
        // 通过标签 id 查询出所有文章 id
        LambdaQueryWrapper<ArticleTag> queryWrapperArticleTag = new LambdaQueryWrapper<>();
        queryWrapperArticleTag.eq(ArticleTag::getTagId, id);
        List<Integer> articleIds = articleTagMapper.selectList(queryWrapperArticleTag).stream()
                .map(ArticleTag::getArticleId)
                .collect(Collectors.toList());

        // 有数据就查询，没有就返回空数组
        if (articleIds.isEmpty()) {
            return emptyArticlePage(pageDTO);
        }

        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.in(ArticleConfig::getArticleId, articleIds);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDraft, false);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDel, false);
        articleIds = articleConfigMapper.selectList(articleConfigLambdaQueryWrapper).stream()
                .map(ArticleConfig::getArticleId).collect(Collectors.toList());

        // 如果过滤后没有文章,直接返回空页
        if (articleIds.isEmpty()) {
            return emptyArticlePage(pageDTO);
        }

        // 数据库分页查询文章（排除 content 大字段）
        Page<Article> page = CommonUtils.buildPage(pageDTO);
        Page<ArticleVO> result;
        List<Article> pageArticles;
        if (page == null) {
            List<Article> all = articleMapper.selectBatchWithoutContent(articleIds);
            result = new Page<>(1, Math.max(1, all.size()));
            result.setTotal(all.size());
            pageArticles = all;
        } else {
            Page<Article> entityPage = articleMapper.selectArticlePage(page, articleIds);
            pageArticles = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }

        // 批量绑定 + 普通用户展示规则（标签页过滤隐藏 + 加密打码）
        List<ArticleVO> vos = batchBindingArticleData(pageArticles);
        List<ArticleVO> filtered = vos.stream()
                .filter(vo -> shouldShowInList(vo, false))
                .map(this::maskIfEncrypted)
                .collect(Collectors.toList());

        result.setRecords(filtered);
        return result;
    }

    /**
     * 是否在列表中展示（管理员可见所有，普通用户不展示隐藏文章）
     */
    private boolean shouldShowInList(ArticleVO article, boolean isAdmin) {
        ArticleConfig config = article.getConfig();
        // 如果没有配置就展示
        if (config == null)
            return true;
        // 管理员可见所有文章
        if (isAdmin)
            return true;
        // 非管理员不能看到隐藏文章
        return !Objects.equals(config.getStatus(), ArticleStatusEnum.HIDE);
    }

    /**
     * 非管理员首页是否展示（在列表可见的基础上，再过滤 no_home）
     */
    private boolean shouldShowOnHomeForNonAdmin(ArticleVO article) {
        ArticleConfig config = article.getConfig();
        if (config == null) {
            return true;
        }
        return !Objects.equals(config.getStatus(), ArticleStatusEnum.NO_HOME);
    }

    /**
     * 如果文章是加密的，则打马赛克提示
     */
    private ArticleVO maskIfEncrypted(ArticleVO article) {
        ArticleConfig config = article.getConfig();
        if (config != null && Boolean.TRUE.equals(config.getIsEncrypt())) {
            article.setDescription("该文章是加密的");
            article.setContent("该文章是加密的");
        }
        return article;
    }

    @Override
    public List<ArticleVO> getRandomArticleList(Integer count) {
        // 一次性从配置表筛选出符合条件的文章ID，避免 N+1 查询
        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDraft, false);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDel, false);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getStatus, ArticleStatusEnum.DEFAULT);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getPassword, "");

        List<Integer> ids = articleConfigMapper.selectList(articleConfigLambdaQueryWrapper)
                .stream()
                .map(ArticleConfig::getArticleId)
                .collect(Collectors.toList());

        // 优化：提前返回
        if (ids.isEmpty())
            return new ArrayList<>();

        // 如果总数不超过 count，沿用原有行为：直接通过 get 返回完整文章数据（包含权限等处理）
        if (ids.size() <= count) {
            return ids.stream()
                    .map(id -> getArticleData(id, ""))
                    .collect(Collectors.toList());
        }

        // 随机打乱文章ID列表并截取前 count 个
        Collections.shuffle(ids, new Random());
        List<Integer> randomArticleIds = ids.subList(0, count);

        // 与原逻辑保持一致，大量数据时仅做绑定，不走 get 的密码/上下篇逻辑
        return batchBindingArticleData(articleMapper.selectBatchWithoutContent(randomArticleIds));
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CACHE_HOT_ARTICLE, key = "#count")
    public List<ArticleVO> getHotArticleList(Integer count) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getView).last("LIMIT " + count);
        // 批量回填，避免逐条 bindingArticleData 的 N+1 查询
        // 缓存说明：浏览量高频变动，靠 60s 短 TTL 更新热度排序；文章增删改时主动 evict
        return batchBindingArticleData(articleMapper.selectList(queryWrapper));
    }

    @Override
    public void recordViewArticleData(Integer id) {
        // 原子自增浏览量
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).setSql("view = view + 1");
        int updated = articleMapper.update(null, updateWrapper);
        if (updated == 0)
            throw new CustomException("获取失败：该文章不存在");

        // 浏览明细落库供数据分析使用，失败不影响浏览量递增
        try {
            ArticleViewLog viewLog = new ArticleViewLog();
            viewLog.setArticleId(id);
            HttpServletRequest request = CommonUtils.getRequest();
            viewLog.setIp(request == null ? null : IpUtils.getRealIp(request));
            viewLog.setCreateTime(System.currentTimeMillis());
            articleViewLogMapper.insert(viewLog);
        } catch (Exception e) {
            log.error("记录文章浏览日志失败: articleId={}", id, e);
        }
    }

    @Override
    public Integer incrementArticleLike(Integer id, Integer count) {
        Article data = articleMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该文章不存在");
        }

        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).setSql("like_count = like_count + " + count);
        articleMapper.update(null, updateWrapper);

        Article updated = articleMapper.selectById(id);
        return updated.getLikeCount() != null ? updated.getLikeCount() : count;
    }

    @Override
    public Integer incrementArticleShare(Integer id, Integer count) {
        Article data = articleMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该文章不存在");
        }

        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).setSql("share_count = share_count + " + count);
        articleMapper.update(null, updateWrapper);

        Article updated = articleMapper.selectById(id);
        return updated.getShareCount() != null ? updated.getShareCount() : count;
    }

    // 关联文章数据
    @Override
    public ArticleVO bindingArticleData(Integer id) {
        Article entity = articleMapper.selectById(id);
        if (entity == null)
            throw new CustomException("获取文章失败：该文章不存在");

        ArticleVO data = new ArticleVO();
        BeanUtils.copyProperties(entity, data);

        // 查询当前文章的分类ID
        LambdaQueryWrapper<ArticleCate> queryWrapperCateIds = new LambdaQueryWrapper<>();
        queryWrapperCateIds.eq(ArticleCate::getArticleId, id);
        List<Integer> cate_ids = articleCateMapper.selectList(queryWrapperCateIds).stream().map(ArticleCate::getCateId)
                .collect(Collectors.toList());

        // 如果有分类，则绑定分类信息（仅返回文章关联的分类，而非整棵分类树）
        if (!cate_ids.isEmpty()) {
            LambdaQueryWrapper<Cate> queryWrapperCateList = new LambdaQueryWrapper<>();
            queryWrapperCateList.in(Cate::getId, cate_ids);
            List<CateVO> cates = cateMapper.selectList(queryWrapperCateList).stream().map(cate -> {
                CateVO cateVO = new CateVO();
                BeanUtils.copyProperties(cate, cateVO);
                return cateVO;
            }).collect(Collectors.toList());
            data.setCateList(cates);
        }

        // 查询当前文章的标签ID
        LambdaQueryWrapper<ArticleTag> queryWrapperTagIds = new LambdaQueryWrapper<>();
        queryWrapperTagIds.eq(ArticleTag::getArticleId, id);
        List<Integer> tag_ids = articleTagMapper.selectList(queryWrapperTagIds).stream().map(ArticleTag::getTagId)
                .collect(Collectors.toList());

        if (!tag_ids.isEmpty()) {
            LambdaQueryWrapper<Tag> queryWrapperTagList = new LambdaQueryWrapper<>();
            queryWrapperTagList.in(Tag::getId, tag_ids);
            List<Tag> tags = tagMapper.selectList(queryWrapperTagList);
            data.setTagList(tags);
        }

        data.setComment(commentMapper.getCommentList(id).size());

        // 查找文章配置
        LambdaQueryWrapper<ArticleConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleConfig::getArticleId, id);
        ArticleConfig articleConfig = articleConfigMapper.selectOne(queryWrapper);
        data.setConfig(articleConfig);

        return data;
    }

    // 过滤文章数据
    @Override
    public LambdaQueryWrapper<Article> queryWrapperArticle(ArticleFilterDTO articleFilterDTO) {
        LambdaQueryWrapper<Article> queryWrapper = getArticleQueryWrapper(articleFilterDTO);

        // 根据分类 id 过滤（cateIds 数组，满足任一分类即可）
        List<Integer> filterCateIds = articleFilterDTO.getCateIds() == null
                ? Collections.emptyList()
                : articleFilterDTO.getCateIds().stream().distinct().collect(Collectors.toList());

        if (!filterCateIds.isEmpty()) {
            LambdaQueryWrapper<ArticleCate> queryWrapperArticleIds = new LambdaQueryWrapper<>();
            queryWrapperArticleIds.in(ArticleCate::getCateId, filterCateIds);
            List<Integer> articleIds = articleCateMapper.selectList(queryWrapperArticleIds).stream()
                    .map(ArticleCate::getArticleId).distinct().collect(Collectors.toList());

            if (!articleIds.isEmpty()) {
                queryWrapper.in(Article::getId, articleIds);
            } else {
                // 添加一个始终为假的条件
                queryWrapper.in(Article::getId, -1); // -1 假设为不存在的ID
            }
        }

        // 根据标签id过滤
        if (articleFilterDTO.getTagId() != null) {
            LambdaQueryWrapper<ArticleTag> queryWrapperArticleIds = new LambdaQueryWrapper<>();
            queryWrapperArticleIds.eq(ArticleTag::getTagId, articleFilterDTO.getTagId());
            List<Integer> articleIds = articleTagMapper.selectList(queryWrapperArticleIds).stream()
                    .map(ArticleTag::getArticleId).collect(Collectors.toList());

            if (!articleIds.isEmpty()) {
                queryWrapper.in(Article::getId, articleIds);
            } else {
                // 添加一个始终为假的条件
                queryWrapper.in(Article::getId, -1); // -1 假设为不存在的ID
            }
        }

        return queryWrapper;
    }

    // 删除文章关联的数据（支持单个和批量）
    public void delArticleCorrelationData(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty())
            return;

        // 删除绑定的分类
        LambdaQueryWrapper<ArticleCate> queryWrapperCate = new LambdaQueryWrapper<>();
        queryWrapperCate.in(ArticleCate::getArticleId, ids);
        articleCateMapper.delete(queryWrapperCate);

        // 删除绑定的标签
        LambdaQueryWrapper<ArticleTag> queryWrapperTag = new LambdaQueryWrapper<>();
        queryWrapperTag.in(ArticleTag::getArticleId, ids);
        articleTagMapper.delete(queryWrapperTag);

        // 删除文章配置
        LambdaQueryWrapper<ArticleConfig> queryWrapperArticleConfig = new LambdaQueryWrapper<>();
        queryWrapperArticleConfig.in(ArticleConfig::getArticleId, ids);
        articleConfigMapper.delete(queryWrapperArticleConfig);
    }

    public void delArticleCorrelationData(Integer id) {
        if (id == null)
            return;
        delArticleCorrelationData(Collections.singletonList(id));
    }

    @Override
    public void importArticleList(MultipartFile[] list) throws IOException {
        if (list == null || list.length == 0)
            throw new CustomException("导入失败：文件列表为空");

        // 验证所有文件格式
        for (MultipartFile file : list) {
            if (file == null || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".md")) {
                throw new CustomException("导入失败：请确保所有文件都是 .md 格式");
            }
        }

        // 如果所有文件格式都正确，则继续处理
        for (MultipartFile file : list) {
            // 读取文件内容
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            // 解析 Markdown 内容
            String[] lines = content.split("\n");
            String title = "";
            String description = "";
            StringBuilder articleContent = new StringBuilder();

            // 提取标题（第一个 # 开头的行）
            for (String line : lines) {
                if (line.startsWith("# ")) {
                    title = line.substring(2).trim();
                    break;
                }
            }

            // 提取描述（第一个空行后的第一段）
            boolean foundDescription = false;
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    foundDescription = true;
                    continue;
                }
                if (foundDescription && !line.startsWith("#")) {
                    description = line.trim();
                    break;
                }
            }

            // 提取文章内容（跳过标题和描述后的所有内容）
            boolean startContent = false;
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    startContent = true;
                    continue;
                }
                if (startContent) {
                    articleContent.append(line).append("\n");
                }
            }

            // 创建文章对象
            ArticleFormDTO article = new ArticleFormDTO();
            article.setTitle(title);
            article.setDescription(description);
            article.setContent(articleContent.toString().trim());
            article.setCreateTime(System.currentTimeMillis());

            // 设置默认分类（这里假设使用 ID 为 1 的分类）
            article.setCateIds(Collections.singletonList(1));

            // 设置默认文章配置
            ArticleConfig config = new ArticleConfig();
            config.setStatus(ArticleStatusEnum.DEFAULT);
            config.setPassword("");
            config.setIsDraft(false);
            config.setIsEncrypt(false);
            config.setIsDel(false);
            article.setConfig(config);

            // 保存文章
            addArticleData(article);
        }
    }

    @Override
    public ResponseEntity<byte[]> exportArticleList(List<Integer> ids) {
        // 创建一个临时目录用于存储导出的Markdown文件
        java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"), "exported_articles");

        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new CustomException("无法创建临时目录");
        }

        if (ids == null || ids.isEmpty()) {
            // 查询所有的文章
            List<Article> list = this.lambdaQuery().select(Article::getId).list();
            if (list == null || list.isEmpty()) {
                throw new CustomException("没有文章可以导出");
            }
            ids = list.stream().map(Article::getId).collect(Collectors.toList());
        }

        try {
            // 遍历文章ID列表，生成Markdown文件
            for (Integer id : ids) {
                Article article = getById(id);
                if (article != null) {
                    String markdownContent = buildMarkdownContent(article);
                    String fileName = sanitizeFileName(article.getTitle()) + ".md";
                    java.io.File markdownFile = new java.io.File(tempDir, fileName);
                    try (java.io.FileWriter writer = new java.io.FileWriter(markdownFile)) {
                        writer.write(markdownContent);
                    } catch (IOException e) {
                        throw new CustomException("写入Markdown文件失败");
                    }
                }
            }

            // 将所有Markdown文件压缩为一个ZIP文件
            ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(zipOutputStream)) {
                for (java.io.File file : Objects.requireNonNull(tempDir.listFiles())) {
                    if (file.isFile() && file.getName().endsWith(".md")) {
                        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zos.putNextEntry(zipEntry);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                            zos.closeEntry();
                        }
                    }
                }
                zos.finish(); // 确保 ZIP 文件正确关闭
            } catch (Exception e) {
                log.error("生成 ZIP 文件失败", e);
                throw new CustomException("生成 ZIP 文件失败");
            }

            // 获取ZIP文件的字节数组
            byte[] zipBytes = zipOutputStream.toByteArray();

            // 删除临时目录及其内容
            java.io.File[] files = tempDir.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    if (!file.delete()) {
                        log.warn("无法删除临时文件: {}", file.getAbsolutePath());
                    }
                }
            }

            if (!tempDir.delete()) {
                log.warn("无法删除临时目录: {}", tempDir.getAbsolutePath());
            }

            // 返回ResponseEntity
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=articles.zip")
                    .body(zipBytes);
        } catch (Exception e) {
            log.error("导出文章失败", e);
            throw new CustomException("导出文章失败");
        }
    }

    /**
     * 构建Markdown格式的文章内容
     */
    private String buildMarkdownContent(Article article) {
        StringBuilder content = new StringBuilder();

        // 添加标题
        content.append("# ").append(article.getTitle()).append("\n\n");

        // 添加描述（如果有）
        if (article.getDescription() != null && !article.getDescription().isEmpty()) {
            content.append(article.getDescription()).append("\n\n");
        }

        // 添加内容
        content.append(article.getContent());

        // 添加元数据（可选）
        content.append("\n\n---\n");
        content.append("导出时间: ").append(LocalDateTime.now()).append("\n");
        content.append("原文ID: ").append(article.getId()).append("\n");

        return content.toString();
    }

    /**
     * 清理文件名，移除非法字符
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "untitled";
        }
        // 替换Windows和Linux文件系统中的非法字符
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
