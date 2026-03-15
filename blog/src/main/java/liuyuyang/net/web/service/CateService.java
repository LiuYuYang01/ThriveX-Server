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

    void delCateData(Integer cid);

    void batchDelCateData(List<Integer> ids);

    Cate getCateData(Integer cid);

    /**
     * 获取分类列表，支持 pattern（list/tree）。不传 page/size 时返回全部，传则分页。
     *
     * @param pattern list: 扁平数组 | tree: 树形结构
     * @param page    页码（从 1 开始），可选
     * @param size    每页数量，可选
     * @return 分页结果（未传 page/size 时为一页全量）
     */
    Page<Cate> list(String pattern, Integer page, Integer size);

    List<CateArticleCount> getCateArticleCount();

    List<Cate> buildCateTreeData(List<Cate> list, Integer level);
}
