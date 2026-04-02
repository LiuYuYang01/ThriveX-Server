package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.cate.CateFilterDTO;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;
import liuyuyang.net.vo.cate.CateVo;

import java.util.List;

public interface CateService extends IService<Cate> {
    // 判断是否存在二级分类
    void isExistTwoCate(Integer cid);

    // 判断该分类中是否有文章
    void isCateArticleCount(Integer cid);

    void delCateData(Integer cid);

    void batchDelCateData(List<Integer> ids);

    Cate getCateData(Integer cid);

    Page<Cate> getCateList(CateFilterDTO cateFilterDTO);

    List<CateArticleCount> getCateArticleCount();

    List<CateVo> getCateTreeChildren(List<Cate> list, Integer id);
}
