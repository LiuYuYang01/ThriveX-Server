package liuyuyang.net.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.dto.album.AlbumCateAddFormDTO;
import liuyuyang.net.model.AlbumCate;
import liuyuyang.net.model.AlbumImage;
import liuyuyang.net.web.mapper.AlbumCateMapper;
import liuyuyang.net.web.mapper.AlbumImageMapper;
import liuyuyang.net.web.service.AlbumCateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlbumCateServiceImpl extends ServiceImpl<AlbumCateMapper, AlbumCate> implements AlbumCateService {
    @Resource
    private AlbumCateMapper albumCateMapper;
    @Resource
    private AlbumImageMapper albumImageMapper;

    @Override
    public void add(AlbumCateAddFormDTO albumCateAddFormDTO) {
        AlbumCate albumCate = BeanUtil.copyProperties(albumCateAddFormDTO, AlbumCate.class);
        albumCateMapper.insert(albumCate);
    }

    @Override
    public void del(Integer id) {
        isExist(id);
        albumCateMapper.deleteById(id);
    }

    @Override
    public void batchDel(List<Integer> ids) {
        isExist(ids);
        albumCateMapper.deleteBatchIds(ids);
    }

    @Override
    public void edit(AlbumCate albumCate) {
        isExist(albumCate.getId());
        updateById(albumCate);
    }

    @Override
    public AlbumCate get(Integer id) {
        return albumCateMapper.selectById(id);
    }

    @Override
    public List<AlbumCate> list() {
        LambdaQueryWrapper<AlbumCate> lambdaQueryAlbumCateWrapper = new LambdaQueryWrapper<>();
        lambdaQueryAlbumCateWrapper.last("ORDER BY RAND()");
        List<AlbumCate> list = albumCateMapper.selectList(lambdaQueryAlbumCateWrapper);

        if (list.isEmpty()) return list;

        // 批量查所有图片
        List<Integer> cateIds = list.stream().map(AlbumCate::getId).collect(Collectors.toList());
        LambdaQueryWrapper<AlbumImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.in(AlbumImage::getCateId, cateIds);
        List<AlbumImage> allImages = albumImageMapper.selectList(imageWrapper);

        Map<Integer, Long> countMap = allImages.stream().collect(java.util.stream.Collectors.groupingBy(AlbumImage::getCateId, java.util.stream.Collectors.counting()));

        for (AlbumCate cate : list) {
            cate.setCount(countMap.getOrDefault(cate.getId(), 0L).intValue());
        }

        return list;
    }

    @Override
    public Page<AlbumCate> paging(Integer page, Integer size) {
        LambdaQueryWrapper<AlbumCate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.last("ORDER BY RAND()");
        Page<AlbumCate> list = page(new Page<>(page, size), lambdaQueryWrapper);

        if (list.getRecords().isEmpty()) return list;

        List<Integer> cateIds = list.getRecords().stream().map(AlbumCate::getId).collect(Collectors.toList());
        LambdaQueryWrapper<AlbumImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.in(AlbumImage::getCateId, cateIds);
        List<AlbumImage> allImages = albumImageMapper.selectList(imageWrapper);

        Map<Integer, Long> countMap = allImages.stream().collect(java.util.stream.Collectors.groupingBy(AlbumImage::getCateId, java.util.stream.Collectors.counting()));

        for (AlbumCate cate : list.getRecords()) {
            cate.setCount(countMap.getOrDefault(cate.getId(), 0L).intValue());
        }

        return list;
    }

    @Override
    public Page<AlbumImage> getImagesByAlbumId(Integer id, Integer page, Integer size) {
        LambdaQueryWrapper<AlbumImage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果 id 为 0，查询全部照片；否则查询指定相册的照片
        if (id != 0) {
            lambdaQueryWrapper.eq(AlbumImage::getCateId, id);
        }
        lambdaQueryWrapper.last("ORDER BY RAND()");
        return albumImageMapper.selectPage(new Page<>(page, size), lambdaQueryWrapper);
    }

    // 判断是否存在
    public void isExist(Integer id) {
        AlbumCate albumCate = this.get(id);
        if (albumCate == null) throw new CustomException(400, "该相册不存在");
    }

    public void isExist(List<Integer> ids) {
        for (Integer id : ids) {
            AlbumCate albumCate = this.get(id);
            if (albumCate == null) throw new CustomException(400, "ID为" + id + "的相册不存在");
        }
    }
}