package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;

import java.util.List;

public interface CateService extends IService<Cate> {
    // 判断是否存在二级分类
    Boolean isExistTwoCate(Integer cid);

    // 判断该分类中是否有文章
    Boolean isCateArticleCount(Integer cid);

    void del(Integer cid);

    void batchDel(List<Integer> ids);

    Cate get(Integer cid);

    /**
     * 获取分类列表，支持 pattern（list/tree）和分页
     *
     * @param pattern list: 扁平数组 | tree: 树形结构
     * @param page    可选，分页时传入
     * @param size    可选，分页时传入
     * @return 传 page&amp;size 时返回 Page，否则返回 List
     */
    Object list(String pattern, Integer page, Integer size);

    List<CateArticleCount> cateArticleCount();

    List<Cate> buildCateTree(List<Cate> list, Integer level);
}
