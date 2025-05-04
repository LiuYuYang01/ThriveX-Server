package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Article;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.article.ArticleFillterVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArticleService extends IService<Article> {
    void add(Article article);

    void del(Integer id, Integer is_del);

    void reduction(Integer id);

    void delBatch(List<Integer> ids);

    void edit(Article article);

    Article get(Integer id, String password);

    List<Article> list(ArticleFillterVo filterVo, String token);

    Page<Article> paging(ArticleFillterVo filterVo, PageVo pageVo, String token);

    Page<Article> getCateArticleList(Integer id, PageVo pageVo);

    Page<Article> getTagArticleList(Integer id, PageVo pageVo);

    List<Article> getRandomArticles(Integer count);

    List<Article> getRecommendedArticles(Integer count);

    void recordView(Integer id);

    Article bindingData(Integer id);

    void importArticle(MultipartFile[] list) throws IOException;

    // void exportArticle(List<Integer> ids);
    ResponseEntity<byte[]> exportArticle(List<Integer> ids);

    QueryWrapper<Article> queryWrapperArticle(ArticleFillterVo filterVo);
}
