package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.article.ArticleFormDTO;
import liuyuyang.net.model.Article;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.article.ArticleFilterDTO;
import liuyuyang.net.vo.article.ArticleVO;
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

    ArticleVO getArticleData(Integer id, String password);

    List<ArticleVO> processArticleData(ArticleFilterDTO filterVo);

    Page<ArticleVO> getArticleList(ArticleFilterDTO filterVo);

    Page<ArticleVO> getCateArticleList(Integer id, PageDTO pageDTO);

    Page<ArticleVO> getTagArticleList(Integer id, PageDTO pageDTO);

    List<ArticleVO> getRandomArticleList(Integer count);

    List<ArticleVO> getHotArticleList(Integer count);

    void recordViewArticleData(Integer id);

    ArticleVO bindingArticleData(Integer id);

    void importArticleList(MultipartFile[] list) throws IOException;

    ResponseEntity<byte[]> exportArticleList(List<Integer> ids);

    LambdaQueryWrapper<Article> queryWrapperArticle(ArticleFilterDTO filterVo);
}
