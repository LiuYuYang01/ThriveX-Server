package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.cate.CateFilterDTO;
import liuyuyang.net.dto.cate.CateFormDTO;
import liuyuyang.net.dto.cate.CateSortDTO;
import liuyuyang.net.enums.cate.CatePatternEnum;
import liuyuyang.net.model.ArticleCate;
import liuyuyang.net.vo.cate.CateVO;
import liuyuyang.net.web.mapper.ArticleCateMapper;
import liuyuyang.net.web.mapper.CateMapper;
import liuyuyang.net.model.Cate;
import liuyuyang.net.vo.cate.CateArticleCountVO;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.web.service.CateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class CateServiceImpl extends ServiceImpl<CateMapper, Cate> implements CateService {
    @Resource
    private CateMapper cateMapper;
    @Resource
    private ArticleCateMapper articleCateMapper;
    @Resource
    private CommonUtils commonUtils;

    // 判断是否存在二级分类，如果有就抛出异常提示先解绑二级分类
    @Override
    public void isExistTwoCate(Integer id) {
        LambdaQueryWrapper<Cate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cate::getLevel, id);
        List<Cate> data = cateMapper.selectList(lambdaQueryWrapper);
        if (!data.isEmpty()) {
            throw new CustomException("ID为：" + id + "的分类中有 " + data.size() + " 个二级分类，请解绑后重试");
        }
    }

    // 判断该分类中是否有文章
    @Override
    public void isCateArticleCount(Integer id) {
        LambdaQueryWrapper<ArticleCate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleCate::getCateId, id);
        List<ArticleCate> data = articleCateMapper.selectList(lambdaQueryWrapper);
        if (!data.isEmpty()) {
            throw new CustomException("ID为：" + id + "的分类中有 " + data.size() + " 篇文章，请删除后重试");
        }
    }

    @Override
    public void addCateData(CateFormDTO cateFormDTO) {
        Cate cate = new Cate();
        BeanUtils.copyProperties(cateFormDTO, cate);
        if (cate.getIsHide() == null) {
            cate.setIsHide(false);
        }
        if (cate.getLevel() == null) {
            cate.setLevel(0);
        }
        if (cate.getOrder() == null || cate.getOrder() == 0) {
            cate.setOrder(nextCateOrder(cate.getLevel()));
        }
        this.save(cate);
    }

    @Override
    public void editCateData(CateFormDTO cateFormDTO) {
        Cate cate = new Cate();
        BeanUtils.copyProperties(cateFormDTO, cate);
        this.updateById(cate);
    }

    @Override
    public void delCateData(Integer id) {
        isExistTwoCate(id);
        isCateArticleCount(id);
        int affected = cateMapper.deleteById(id);
        if (affected == 0) {
            throw new CustomException("该分类不存在");
        }
    }

    @Override
    public void batchDelCateData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 批量校验：存在子分类的父级 ID（使用 BaseMapper 的 selectList）
        List<Cate> children = cateMapper.selectList(new LambdaQueryWrapper<Cate>().in(Cate::getLevel, ids));
        Map<Integer, Long> childrenByParent = children.stream()
                .collect(Collectors.groupingBy(Cate::getLevel, Collectors.counting()));
        for (Map.Entry<Integer, Long> e : childrenByParent.entrySet()) {
            throw new CustomException("ID为：" + e.getKey() + "的分类中有 " + e.getValue() + " 个二级分类，请解绑后重试");
        }

        // 批量校验：存在文章的分类 ID（使用 BaseMapper 的 selectList）
        List<ArticleCate> articles = articleCateMapper
                .selectList(new LambdaQueryWrapper<ArticleCate>().in(ArticleCate::getCateId, ids));
        Map<Integer, Long> articlesByCate = articles.stream()
                .collect(Collectors.groupingBy(ArticleCate::getCateId, Collectors.counting()));
        for (Map.Entry<Integer, Long> e : articlesByCate.entrySet()) {
            throw new CustomException("ID为：" + e.getKey() + "的分类中有 " + e.getValue() + " 篇文章，请删除后重试");
        }

        // 校验要删除的分类是否都存在
        long existCount = count(new LambdaQueryWrapper<Cate>().in(Cate::getId, ids));
        if (existCount != ids.size()) {
            throw new CustomException("有 " + (ids.size() - (int) existCount) + " 个分类不存在");
        }

        // 批量删除
        removeByIds(ids);
    }

    @Override
    public CateVO getCateData(Integer id) {
        Cate cate = cateMapper.selectById(id);
        if (cate == null) {
            throw new CustomException("该分类不存在");
        }
        if (!CommonUtils.isAdmin() && Boolean.TRUE.equals(cate.getIsHide())) {
            throw new CustomException("该分类不存在");
        }

        CateVO cateVO = new CateVO();
        BeanUtils.copyProperties(cate, cateVO);

        LambdaQueryWrapper<Cate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Cate::getOrder);
        List<Cate> all = cateMapper.selectList(wrapper);

        // 子分类树（原逻辑已组装到 cateVO，此前误返回实体 cate，导致前端拿不到 children）
        cateVO.setChildren(getCateTreeChildren(all, id));

        // 与列表接口一致：附带该分类下的文章数量
        Map<Integer, Integer> articleCountByCateId = articleCateMapper
                .getCateArticleCountByCateId()
                .stream()
                .collect(Collectors.toMap(
                        CateArticleCountVO::getCid,
                        CateArticleCountVO::getCount
                ));
        cateVO.setCount(articleCountByCateId.getOrDefault(id, 0));

        return cateVO;
    }

    @Override
    public Page<CateVO> getCateList(CateFilterDTO cateFilterDTO) {
        boolean isAdmin = CommonUtils.isAdmin();
        LambdaQueryWrapper<Cate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Cate::getOrder);
        if (!isAdmin) {
            wrapper.and(w -> w.eq(Cate::getIsHide, false).or().isNull(Cate::getIsHide));
        }

        // 获取文章数量
        Map<Integer, Integer> articleCountByCateId = articleCateMapper
                .getCateArticleCountByCateId()
                .stream()
                .collect(Collectors.toMap(
                        CateArticleCountVO::getCid,
                        CateArticleCountVO::getCount
                ));

        // 获取分类列表
        List<Cate> raw = cateMapper.selectList(wrapper);
        List<CateVO> listVos = raw.stream().map(cate -> {
            CateVO cateVO = new CateVO();
            BeanUtils.copyProperties(cate, cateVO);
            // 设置文章数量
            cateVO.setCount(articleCountByCateId.getOrDefault(cate.getId(), 0));
            return cateVO;
        }).collect(Collectors.toList());

        // 如果是 tree 模式则构建树形结构，否则列表结构
        List<CateVO> arr;
        if (cateFilterDTO.getPattern() == CatePatternEnum.LIST) {
            arr = listVos;
        } else {
            arr = new ArrayList<>(getCateTreeChildren(raw, 0));
            fillTreeCount(arr, articleCountByCateId);
        }

        return commonUtils.paginate(cateFilterDTO, arr);
    }

    // 基于父级分类的 id 递归获取它的所有子分类
    @Override
    public List<CateVO> getCateTreeChildren(List<Cate> list, Integer level) {
        boolean isAdmin = CommonUtils.isAdmin();
        return getCateTreeChildren(list, level, isAdmin);
    }

    private List<CateVO> getCateTreeChildren(List<Cate> list, Integer level, boolean isAdmin) {
        List<CateVO> children = new ArrayList<>();
        for (Cate cate : list) {
            if (Objects.equals(cate.getLevel(), level)) {
                if (!isAdmin && Boolean.TRUE.equals(cate.getIsHide())) {
                    continue;
                }
                CateVO cateVO = new CateVO();
                BeanUtils.copyProperties(cate, cateVO);
                cateVO.setChildren(getCateTreeChildren(list, cate.getId(), isAdmin));
                children.add(cateVO);
            }
        }
        children.sort(Comparator.comparingInt(c -> c.getOrder() == null ? 0 : c.getOrder()));
        return children;
    }

    @Override
    public void sortCateData(CateSortDTO cateSortDTO) {
        if (cateSortDTO == null || cateSortDTO.getLevel() == null) {
            throw new CustomException("请提供上级分类 ID");
        }
        List<Integer> ids = cateSortDTO.getIds();
        if (ids == null || ids.isEmpty()) {
            throw new CustomException("请提供排序后的分类 ID 列表");
        }
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new CustomException("分类 ID 不能重复");
        }

        Integer parentLevel = cateSortDTO.getLevel();
        long existCount = count(new LambdaQueryWrapper<Cate>()
                .eq(Cate::getLevel, parentLevel)
                .in(Cate::getId, ids));
        if (existCount != ids.size()) {
            throw new CustomException("有 " + (ids.size() - (int) existCount) + " 个分类不存在或不属于该层级");
        }

        long siblingCount = count(new LambdaQueryWrapper<Cate>().eq(Cate::getLevel, parentLevel));

        for (int i = 0; i < ids.size(); i++) {
            Cate cate = new Cate();
            cate.setId(ids.get(i));
            cate.setOrder(i + 1);
            updateById(cate);
        }
    }

    private int nextCateOrder(Integer level) {
        Cate last = getOne(new LambdaQueryWrapper<Cate>()
                .select(Cate::getOrder)
                .eq(Cate::getLevel, level == null ? 0 : level)
                .orderByDesc(Cate::getOrder)
                .last("LIMIT 1"), false);
        return last == null || last.getOrder() == null ? 1 : last.getOrder() + 1;
    }

    private void fillTreeCount(List<CateVO> tree, Map<Integer, Integer> articleCountByCateId) {
        for (CateVO node : tree) {
            node.setCount(articleCountByCateId.getOrDefault(node.getId(), 0));
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                fillTreeCount(node.getChildren(), articleCountByCateId);
            }
        }
    }
}
