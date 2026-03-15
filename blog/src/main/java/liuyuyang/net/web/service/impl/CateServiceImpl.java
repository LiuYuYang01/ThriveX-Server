package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.CommonUtils;
import liuyuyang.net.model.ArticleCate;
import liuyuyang.net.web.mapper.ArticleCateMapper;
import liuyuyang.net.web.mapper.CateMapper;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.web.service.CateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    // 判断是否存在二级分类
    @Override
    public Boolean isExistTwoCate(Integer id) {
        LambdaQueryWrapper<Cate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cate::getLevel, id);
        List<Cate> data = cateMapper.selectList(lambdaQueryWrapper);
        if (!data.isEmpty()) {
            throw new CustomException(400, "ID为：" + id + "的分类中有 " + data.size() + " 个二级分类，请解绑后重试");
        }
        return true;
    }

    // 判断该分类中是否有文章
    @Override
    public Boolean isCateArticleCount(Integer id) {
        LambdaQueryWrapper<ArticleCate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleCate::getCateId, id);
        List<ArticleCate> data = articleCateMapper.selectList(lambdaQueryWrapper);
        if (!data.isEmpty()) {
            throw new CustomException(400, "ID为：" + id + "的分类中有 " + data.size() + " 篇文章，请删除后重试");
        }
        return true;
    }

    @Override
    public void delCateData(Integer id) {
        isExistTwoCate(id);
        isCateArticleCount(id);
        int affected = cateMapper.deleteById(id);
        if (affected == 0) {
            throw new CustomException(400, "该分类不存在");
        }
    }

    @Override
    public void batchDelCateData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 批量校验：存在子分类的父级 ID（使用 BaseMapper 的 selectList）
        List<Cate> children = cateMapper.selectList(new LambdaQueryWrapper<Cate>().in(Cate::getLevel, ids));
        Map<Integer, Long> childrenByParent = children.stream().collect(Collectors.groupingBy(Cate::getLevel, Collectors.counting()));
        for (Map.Entry<Integer, Long> e : childrenByParent.entrySet()) {
            throw new CustomException(400, "ID为：" + e.getKey() + "的分类中有 " + e.getValue() + " 个二级分类，请解绑后重试");
        }

        // 批量校验：存在文章的分类 ID（使用 BaseMapper 的 selectList）
        List<ArticleCate> articles = articleCateMapper.selectList(new LambdaQueryWrapper<ArticleCate>().in(ArticleCate::getCateId, ids));
        Map<Integer, Long> articlesByCate = articles.stream().collect(Collectors.groupingBy(ArticleCate::getCateId, Collectors.counting()));
        for (Map.Entry<Integer, Long> e : articlesByCate.entrySet()) {
            throw new CustomException(400, "ID为：" + e.getKey() + "的分类中有 " + e.getValue() + " 篇文章，请删除后重试");
        }

        // 校验要删除的分类是否都存在
        long existCount = count(new LambdaQueryWrapper<Cate>().in(Cate::getId, ids));
        if (existCount != ids.size()) {
            throw new CustomException(400, "有 " + (ids.size() - (int) existCount) + " 个分类不存在");
        }

        // 批量删除
        removeByIds(ids);
    }

    @Override
    public Cate getCateData(Integer id) {
        Cate cate = cateMapper.selectById(id);
        if (cate == null) {
            throw new CustomException(400, "该分类不存在");
        }
        LambdaQueryWrapper<Cate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Cate::getOrder);
        List<Cate> all = cateMapper.selectList(wrapper);
        cate.setChildren(buildCateTreeData(all, id));
        return cate;
    }

    @Override
    public Page<Cate> list(String pattern, Integer page, Integer size) {
        LambdaQueryWrapper<Cate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Cate::getOrder);
        List<Cate> raw = cateMapper.selectList(wrapper);

        // 如果是 tree 模式则构建树形，否则用扁平列表
        List<Cate> arr = Objects.equals(pattern, "tree") ? buildCateTreeData(raw, 0) : raw;

        // 不传 page/size 则返回全部
        if (page == null || size == null) {
            Page<Cate> result = new Page<>(1, arr.size());
            result.setRecords(new ArrayList<>(arr));
            result.setTotal(arr.size());
            return result;
        }

        PageVo pageVo = new PageVo();
        pageVo.setPage(Math.max(1, page));
        pageVo.setSize(Math.max(1, size));
        return commonUtils.getPageData(pageVo, arr);
    }

    @Override
    public List<CateArticleCount> getCateArticleCount() {
        return cateMapper.cateArticleCount();
    }

    @Override
    public List<Cate> buildCateTreeData(List<Cate> list, Integer level) {
        List<Cate> children = new ArrayList<>();
        for (Cate cate : list) {
            if (Objects.equals(cate.getLevel(), level)) {
                cate.setChildren(buildCateTreeData(list, cate.getId()));
                children.add(cate);
            }
        }
        return children;
    }
}