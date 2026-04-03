package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.cate.CateFilterDTO;
import liuyuyang.net.dto.cate.CateFormDTO;
import liuyuyang.net.model.Cate;
import liuyuyang.net.vo.cate.CateVO;

import java.util.List;

public interface CateService extends IService<Cate> {
    // 判断是否存在二级分类
    void isExistTwoCate(Integer cid);

    // 判断该分类中是否有文章
    void isCateArticleCount(Integer cid);

    void addCateData(CateFormDTO cateFormDTO);

    void delCateData(Integer cid);

    void batchDelCateData(List<Integer> ids);

    void editCateData(CateFormDTO cateFormDTO);

    CateVO getCateData(Integer cid);

    Page<CateVO> getCateList(CateFilterDTO cateFilterDTO);

    List<CateVO> getCateTreeChildren(List<Cate> list, Integer id);
}
