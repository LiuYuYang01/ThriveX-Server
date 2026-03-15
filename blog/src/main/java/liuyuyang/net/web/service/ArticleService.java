package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.article.ArticleFormDTO;
import liuyuyang.net.model.Article;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.article.ArticleFilterVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArticleService extends IService<Article> {
    void addArticleData(ArticleFormDTO articleFormDTO);

    void delArticleData(Integer id, Integer is_del);

    void recoveryArticleData(Integer id);

    void delBatchArticleData(List<Integer> ids);

    void editArticleData(ArticleFormDTO articleFormDTO);

    Article getArticleData(Integer id, String password);

    List<Article> processArticleData(ArticleFilterVo filterVo, String token);

    Page<Article> getArticleList(ArticleFilterVo filterVo, String token);

    Page<Article> getCateArticleList(Integer id, PageVo pageVo);

    Page<Article> getTagArticleList(Integer id, PageVo pageVo);

    List<Article> getRandomArticleList(Integer count);

    List<Article> getHotArticleList(Integer count);

    void recordViewArticleData(Integer id);

    Article bindingArticleData(Integer id);

    void importArticleList(MultipartFile[] list) throws IOException;

    ResponseEntity<byte[]> exportArticleList(List<Integer> ids);

    QueryWrapper<Article> queryWrapperArticle(ArticleFilterVo filterVo);
}
