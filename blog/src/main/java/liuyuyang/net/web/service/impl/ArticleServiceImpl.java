package liuyuyang.net.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.CommonUtils;
import liuyuyang.net.dto.article.ArticleFormDTO;
import liuyuyang.net.model.*;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.article.ArticleFilterVo;
import liuyuyang.net.web.mapper.*;
import liuyuyang.net.web.service.ArticleCateService;
import liuyuyang.net.web.service.ArticleService;
import liuyuyang.net.web.service.ArticleTagService;
import liuyuyang.net.web.service.CateService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    private static QueryWrapper<Article> getArticleQueryWrapper(ArticleFilterVo filterVo) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        // 根据关键字通过标题过滤出对应文章数据
        if (filterVo.getKey() != null && !filterVo.getKey().isEmpty()) {
            queryWrapper.like("title", "%" + filterVo.getKey() + "%");
        }

        // 根据开始与结束时间过滤
        if (filterVo.getStartDate() != null && filterVo.getEndDate() != null) {
            queryWrapper.between("create_time", filterVo.getStartDate(), filterVo.getEndDate());
        } else if (filterVo.getStartDate() != null) {
            queryWrapper.ge("create_time", filterVo.getStartDate());
        } else if (filterVo.getEndDate() != null) {
            queryWrapper.le("create_time", filterVo.getEndDate());
        }
        return queryWrapper;
    }

    @Override
    public void addArticleData(ArticleFormDTO articleFormDTO) {
        Article article = BeanUtil.copyProperties(articleFormDTO, Article.class);
        articleMapper.insert(article);

        // 新增分类
        List<Integer> cateIdList = article.getCateIds();
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
        List<Integer> tagIdList = article.getTagIds();

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
        ArticleConfig config = article.getConfig();
        ArticleConfig articleConfig = new ArticleConfig();
        articleConfig.setArticleId(article.getId());
        articleConfig.setStatus(config.getStatus());
        articleConfig.setPassword(config.getPassword());
        articleConfig.setIsDraft(article.getConfig().getIsDraft());
        articleConfig.setIsEncrypt(article.getConfig().getIsEncrypt());
        articleConfig.setIsDel(0);

        articleConfigMapper.insert(articleConfig);
    }

    @Override
    public void delArticleData(Integer id, Integer is_del) {
        Article article = articleMapper.selectById(id);

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
            articleConfig.setIsDel(1);
            articleConfigMapper.updateById(articleConfig);
        }

        if (is_del != 0 && is_del != 1) {
            throw new CustomException(400, "参数有误：请选择是否严格删除");
        }
    }

    @Override
    public void recoveryArticleData(Integer id) {
        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getArticleId, id);
        ArticleConfig articleConfig = articleConfigMapper.selectOne(articleConfigLambdaQueryWrapper);
        articleConfig.setIsDel(0);
        articleConfigMapper.updateById(articleConfig);
    }

    @Override
    public void delBatchArticleData(List<Integer> ids) {
        delArticleCorrelationData(ids);

        // 批量删除文章
        if (ids == null || ids.isEmpty())
            return;

        QueryWrapper<Article> queryWrapperArticle = new QueryWrapper<>();
        queryWrapperArticle.in("id", ids);
        articleMapper.delete(queryWrapperArticle);
    }

    @Override
    public void editArticleData(ArticleFormDTO articleFormDTO) {
        if (articleFormDTO.getCateIds() == null || articleFormDTO.getCateIds().isEmpty())
            throw new CustomException(400, "编辑失败：请绑定分类");

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
        articleConfig.setStatus(config.getStatus());
        articleConfig.setPassword(config.getPassword());
        articleConfig.setIsDraft(config.getIsDraft());
        articleConfig.setIsEncrypt(config.getIsEncrypt());
        articleConfig.setIsDel(0);
        articleConfigMapper.insert(articleConfig);

        Article article = BeanUtil.copyProperties(articleFormDTO, Article.class);

        // 修改文章
        articleMapper.updateById(article);
    }

    @Override
    public Article getArticleData(Integer id, String password) {
        Article data = bindingArticleData(id);

        String description = data.getDescription();
        String content = data.getContent();
        // todo ps by:laifeng 这里需要优化，
        // 对于角色判断，请将角色逻辑移到controller层，不要在service中进行，而且可以通过aop进行操作，避免重复判断
        String token = CommonUtils.getHeader("Authorization");
        boolean isAdmin = !"".equals(token) && CommonUtils.isAdmin(token);

        ArticleConfig config = data.getConfig();

        if (data.getConfig().getIsEncrypt() == 0 && !password.isEmpty()) {
            throw new CustomException(610, "该文章不需要访问密码");
        }

        // 管理员可以查看任何权限的文章
        if (!isAdmin) {
            if (data.getConfig().getIsDel() == 1) {
                throw new CustomException(404, "该文章已被删除");
            }

            if ("hide".equals(config.getStatus())) {
                throw new CustomException(611, "该文章已被隐藏");
            }

            // 如果有密码就必须通过密码才能查看
            if (data.getConfig().getIsEncrypt() == 1) {
                // 如果需要访问密码且没有传递密码参数
                if (password.isEmpty()) {
                    throw new CustomException(612, "请输入文章访问密码");
                }

                data.setDescription("该文章需要密码才能查看");
                data.setContent("该文章需要密码才能查看");

                // 验证密码是否正确
                if (config.getPassword().equals(password)) {
                    data.setDescription(description);
                    data.setContent(content);
                } else {
                    throw new CustomException(613, "文章访问密码错误");
                }
            }
        }

        // 获取当前文章的创建时间
        String createTime = data.getCreateTime();

        // 查询上一篇文章
        QueryWrapper<Article> prevQueryWrapper = new QueryWrapper<>();
        prevQueryWrapper.lt("create_time", createTime).orderByDesc("create_time").last("LIMIT 1");
        Article prevArticle = articleMapper.selectOne(prevQueryWrapper);

        if (prevArticle != null) {
            // 检查文章配置
            QueryWrapper<ArticleConfig> prevConfigWrapper = new QueryWrapper<>();
            prevConfigWrapper.eq("article_id", prevArticle.getId()).eq("is_del", 0);
            ArticleConfig prevConfig = articleConfigMapper.selectOne(prevConfigWrapper);

            if (prevConfig != null) {
                Map<String, Object> resultPrev = new HashMap<>();
                resultPrev.put("id", prevArticle.getId());
                resultPrev.put("title", prevArticle.getTitle());
                data.setPrev(resultPrev);
            }
        }

        // 查询下一篇文章
        QueryWrapper<Article> nextQueryWrapper = new QueryWrapper<>();
        nextQueryWrapper.gt("create_time", createTime).orderByAsc("create_time").last("LIMIT 1");
        Article nextArticle = articleMapper.selectOne(nextQueryWrapper);

        if (nextArticle != null) {
            // 检查文章配置
            QueryWrapper<ArticleConfig> nextConfigWrapper = new QueryWrapper<>();
            nextConfigWrapper.eq("article_id", nextArticle.getId()).eq("is_del", 0);
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
    public List<Article> processArticleData(ArticleFilterVo filterVo, String token) {
        // 首先根据文章配置表的条件筛选出符合条件的文章ID
        QueryWrapper<ArticleConfig> configQueryWrapper = new QueryWrapper<>();

        // 根据草稿状态筛选
        if (filterVo.getIsDraft() != null) {
            configQueryWrapper.eq("is_draft", filterVo.getIsDraft());
        }

        // 根据删除状态筛选
        if (filterVo.getIsDel() != null) {
            configQueryWrapper.eq("is_del", filterVo.getIsDel());
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
        QueryWrapper<Article> queryWrapper = queryWrapperArticle(filterVo);
        queryWrapper.in("id", articleIds);
        List<Article> list = articleMapper.selectList(queryWrapper);

        boolean isAdmin = CommonUtils.isAdmin(token);

        // 绑定数据 + 统一的展示规则（过滤隐藏文章 + 处理加密文章）
        return list.stream()
                .map(article -> bindingArticleData(article.getId()))
                .filter(article -> shouldShowInList(article, isAdmin))
                .map(this::maskIfEncrypted)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Article> getArticleList(ArticleFilterVo filterVo, String token) {
        List<Article> list = processArticleData(filterVo, token);
        boolean isAdmin = CommonUtils.isAdmin(token);
        if (!isAdmin) {
            // 统一使用展示规则控制首页可见性
            list = list.stream()
                    .filter(this::shouldShowOnHomeForNonAdmin)
                    .collect(Collectors.toList());
        }

        // 不传 page/size 则返回全部
        if (filterVo.getPage() == null || filterVo.getSize() == null) {
            Page<Article> result = new Page<>(1, list.size());
            result.setRecords(new ArrayList<>(list));
            result.setTotal(list.size());
            return result;
        }

        PageVo pageVo = new PageVo();
        pageVo.setPage(Math.max(1, filterVo.getPage()));
        pageVo.setSize(Math.max(1, filterVo.getSize()));
        return commonUtils.getPageData(pageVo, list);
    }

    // 获取指定分类中所有文章
    @Override
    public Page<Article> getCateArticleList(Integer id, PageVo pageVo) {
        int p = pageVo.getPage() != null ? Math.max(1, pageVo.getPage()) : 1;
        int s = pageVo.getSize() != null ? Math.max(1, pageVo.getSize()) : 5;
        
        // 通过分类 id 查询出所有文章id
        QueryWrapper<ArticleCate> queryWrapperArticleCate = new QueryWrapper<>();
        queryWrapperArticleCate.eq("cate_id", id); // 修改in为eq,因为只查询单个分类
        List<Integer> articleIds = articleCateMapper.selectList(queryWrapperArticleCate).stream()
                .map(ArticleCate::getArticleId)
                .collect(Collectors.toList());

        // 有数据就查询，没有就返回空数组
        if (articleIds.isEmpty()) {
            return new Page<>(p, s, 0);
        }

        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.in(ArticleConfig::getArticleId, articleIds);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDraft, 0);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDel, 0);
        articleIds = articleConfigMapper.selectList(articleConfigLambdaQueryWrapper).stream()
                .map(ArticleConfig::getArticleId).collect(Collectors.toList());

        // 如果过滤后没有文章,直接返回空页
        if (articleIds.isEmpty()) {
            return new Page<>(p, s, 0);
        }

        // 构建文章查询条件
        QueryWrapper<Article> queryWrapperArticle = new QueryWrapper<>();
        queryWrapperArticle.in("id", articleIds);
        queryWrapperArticle.orderByDesc("create_time");

        // 查询文章列表
        Page<Article> page = new Page<>(p, s);
        articleMapper.selectPage(page, queryWrapperArticle);

        // 绑定数据并处理加密/隐藏文章
        return processArticlePage(page);
    }

    @Override
    public Page<Article> getTagArticleList(Integer id, PageVo pageVo) {
        int p = pageVo.getPage() != null ? Math.max(1, pageVo.getPage()) : 1;
        int s = pageVo.getSize() != null ? Math.max(1, pageVo.getSize()) : 5;

        // 通过标签 id 查询出所有文章 id
        QueryWrapper<ArticleTag> queryWrapperArticleTag = new QueryWrapper<>();
        queryWrapperArticleTag.eq("tag_id", id);
        List<Integer> articleIds = articleTagMapper.selectList(queryWrapperArticleTag).stream()
                .map(ArticleTag::getArticleId)
                .collect(Collectors.toList());

        // 有数据就查询，没有就返回空数组
        if (articleIds.isEmpty()) {
            return new Page<>(p, s, 0);
        }

        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.in(ArticleConfig::getArticleId, articleIds);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDraft, 0);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDel, 0);
        articleIds = articleConfigMapper.selectList(articleConfigLambdaQueryWrapper).stream()
                .map(ArticleConfig::getArticleId).collect(Collectors.toList());

        // 如果过滤后没有文章,直接返回空页
        if (articleIds.isEmpty()) {
            return new Page<>(p, s, 0);
        }

        // 构建文章查询条件
        QueryWrapper<Article> queryWrapperArticle = new QueryWrapper<>();
        queryWrapperArticle.in("id", articleIds).orderByDesc("create_time");

        // 查询文章列表
        Page<Article> page = new Page<>(p, s);
        articleMapper.selectPage(page, queryWrapperArticle);

        // 绑定数据并处理加密/隐藏文章
        return processArticlePage(page);
    }

    /**
     * 公共文章分页结果处理：绑定数据、处理加密文章、过滤隐藏文章
     */
    private Page<Article> processArticlePage(Page<Article> page) {
        page.setRecords(
                page.getRecords().stream()
                        .map(article -> {
                            Article boundArticle = bindingArticleData(article.getId());
                            // 分类/标签页默认按普通用户规则展示
                            if (!shouldShowInList(boundArticle, false)) {
                                return null;
                            }
                            return maskIfEncrypted(boundArticle);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
        return page;
    }

    /**
     * 是否在列表中展示（管理员可见所有，普通用户不展示隐藏文章）
     */
    private boolean shouldShowInList(Article article, boolean isAdmin) {
        ArticleConfig config = article.getConfig();
        // 如果没有配置就展示
        if (config == null)
            return true;
        // 管理员可见所有文章
        if (isAdmin)
            return true;
        // 非管理员不能看到隐藏文章
        return !Objects.equals(config.getStatus(), "hide");
    }

    /**
     * 非管理员首页是否展示（在列表可见的基础上，再过滤 no_home）
     */
    private boolean shouldShowOnHomeForNonAdmin(Article article) {
        ArticleConfig config = article.getConfig();
        if (config == null) {
            return true;
        }
        return !Objects.equals(config.getStatus(), "no_home");
    }

    /**
     * 如果文章是加密的，则打马赛克提示
     */
    private Article maskIfEncrypted(Article article) {
        ArticleConfig config = article.getConfig();
        if (config != null && config.getIsEncrypt() == 1) {
            article.setDescription("该文章是加密的");
            article.setContent("该文章是加密的");
        }
        return article;
    }

    @Override
    public List<Article> getRandomArticleList(Integer count) {
        // 一次性从配置表筛选出符合条件的文章ID，避免 N+1 查询
        LambdaQueryWrapper<ArticleConfig> articleConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDraft, 0);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getIsDel, 0);
        articleConfigLambdaQueryWrapper.eq(ArticleConfig::getStatus, "default");
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
        return randomArticleIds.stream()
                .map(this::bindingArticleData)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getHotArticleList(Integer count) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("view").last("LIMIT " + count);
        return articleMapper.selectList(queryWrapper);
    }

    @Override
    public void recordViewArticleData(Integer id) {
        Article data = articleMapper.selectById(id);
        if (data == null)
            throw new CustomException(400, "获取失败：该文章不存在");
        data.setView(data.getView() + 1);
        articleMapper.updateById(data);
    }

    // 关联文章数据
    @Override
    public Article bindingArticleData(Integer id) {
        Article data = articleMapper.selectById(id);

        if (data == null)
            throw new CustomException(400, "获取文章失败：该文章不存在");

        // 查询当前文章的分类ID
        QueryWrapper<ArticleCate> queryWrapperCateIds = new QueryWrapper<>();
        queryWrapperCateIds.eq("article_id", id);
        List<Integer> cate_ids = articleCateMapper.selectList(queryWrapperCateIds).stream().map(ArticleCate::getCateId)
                .collect(Collectors.toList());

        // 如果有分类，则绑定分类信息
        if (!cate_ids.isEmpty()) {
            QueryWrapper<Cate> queryWrapperCateList = new QueryWrapper<>();
            queryWrapperCateList.in("id", cate_ids);
            List<Cate> cates = cateService.buildCateTreeData(cateMapper.selectList(queryWrapperCateList), 0);
            data.setCateList(cates);
        }

        // 查询当前文章的标签ID
        QueryWrapper<ArticleTag> queryWrapperTagIds = new QueryWrapper<>();
        queryWrapperTagIds.eq("article_id", id);
        List<Integer> tag_ids = articleTagMapper.selectList(queryWrapperTagIds).stream().map(ArticleTag::getTagId)
                .collect(Collectors.toList());

        if (!tag_ids.isEmpty()) {
            QueryWrapper<Tag> queryWrapperTagList = new QueryWrapper<>();
            queryWrapperTagList.in("id", tag_ids);
            List<Tag> tags = tagMapper.selectList(queryWrapperTagList);
            data.setTagList(tags);
        }

        data.setComment(commentMapper.getCommentList(id).size());

        // 查找文章配置
        QueryWrapper<ArticleConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", id);
        ArticleConfig articleConfig = articleConfigMapper.selectOne(queryWrapper);
        data.setConfig(articleConfig);

        return data;
    }

    // 过滤文章数据
    @Override
    public QueryWrapper<Article> queryWrapperArticle(ArticleFilterVo filterVo) {
        QueryWrapper<Article> queryWrapper = getArticleQueryWrapper(filterVo);

        // 根据分类id过滤
        if (filterVo.getCateId() != null) {
            QueryWrapper<ArticleCate> queryWrapperArticleIds = new QueryWrapper<>();
            queryWrapperArticleIds.eq("cate_id", filterVo.getCateId());
            List<Integer> articleIds = articleCateMapper.selectList(queryWrapperArticleIds).stream()
                    .map(ArticleCate::getArticleId).collect(Collectors.toList());

            if (!articleIds.isEmpty()) {
                queryWrapper.in("id", articleIds);
            } else {
                // 添加一个始终为假的条件
                queryWrapper.in("id", -1); // -1 假设为不存在的ID
            }
        }

        // 根据标签id过滤
        if (filterVo.getTagId() != null) {
            QueryWrapper<ArticleTag> queryWrapperArticleIds = new QueryWrapper<>();
            queryWrapperArticleIds.eq("tag_id", filterVo.getTagId());
            List<Integer> articleIds = articleTagMapper.selectList(queryWrapperArticleIds).stream()
                    .map(ArticleTag::getArticleId).collect(Collectors.toList());

            if (!articleIds.isEmpty()) {
                queryWrapper.in("id", articleIds);
            } else {
                // 添加一个始终为假的条件
                queryWrapper.in("id", -1); // -1 假设为不存在的ID
            }
        }

        return queryWrapper;
    }

    // 删除文章关联的数据（支持单个和批量）
    public void delArticleCorrelationData(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty())
            return;

        // 删除绑定的分类
        QueryWrapper<ArticleCate> queryWrapperCate = new QueryWrapper<>();
        queryWrapperCate.in("article_id", ids);
        articleCateMapper.delete(queryWrapperCate);

        // 删除绑定的标签
        QueryWrapper<ArticleTag> queryWrapperTag = new QueryWrapper<>();
        queryWrapperTag.in("article_id", ids);
        articleTagMapper.delete(queryWrapperTag);

        // 删除文章配置
        QueryWrapper<ArticleConfig> queryWrapperArticleConfig = new QueryWrapper<>();
        queryWrapperArticleConfig.in("article_id", ids);
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
            throw new CustomException(400, "导入失败：文件列表为空");

        // 验证所有文件格式
        for (MultipartFile file : list) {
            if (file == null || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".md")) {
                throw new CustomException(400, "导入失败：请确保所有文件都是 .md 格式");
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
            article.setCreateTime(String.valueOf(LocalDateTime.now()));

            // 设置默认分类（这里假设使用 ID 为 1 的分类）
            article.setCateIds(Collections.singletonList(1));

            // 设置默认文章配置
            ArticleConfig config = new ArticleConfig();
            config.setStatus("default");
            config.setPassword("");
            config.setIsDraft(0);
            config.setIsEncrypt(0);
            config.setIsDel(0);
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