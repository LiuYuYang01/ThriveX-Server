package liuyuyang.net.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.dto.album.AlbumImageAddFormDTO;
import liuyuyang.net.model.AlbumImage;
import liuyuyang.net.web.mapper.AlbumImageMapper;
import liuyuyang.net.web.service.AlbumImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class AlbumImageServiceImpl extends ServiceImpl<AlbumImageMapper, AlbumImage> implements AlbumImageService {
    @Resource
    private AlbumImageMapper albumImageMapper;

    @Override
    public void add(AlbumImageAddFormDTO albumImageAddFormDTO) {
        AlbumImage albumImage = BeanUtil.copyProperties(albumImageAddFormDTO, AlbumImage.class);
        albumImageMapper.insert(albumImage);
    }

    @Override
    public void del(Integer id) {
        isExist(id);
        albumImageMapper.deleteById(id);
    }

    @Override
    public void batchDel(List<Integer> ids) {
        isExist(ids);
        albumImageMapper.deleteBatchIds(ids);
    }

    @Override
    public void edit(AlbumImage albumImage) {
        isExist(albumImage.getCateId());
        updateById(albumImage);
    }

    @Override
    public AlbumImage get(Integer id) {
        AlbumImage albumImage = albumImageMapper.selectById(id);
        if (albumImage == null) throw new CustomException(400, "该照片不存在");
        return albumImage;
    }

    @Override
    public List<AlbumImage> list() {
        LambdaQueryWrapper<AlbumImage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.last("ORDER BY RAND()");
        return albumImageMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public Page<AlbumImage> paging(Integer page, Integer size) {
        LambdaQueryWrapper<AlbumImage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.last("ORDER BY RAND()");
        return page(new Page<>(page, size), lambdaQueryWrapper);
    }

    public void isExist(Integer id) {
        AlbumImage albumImage = this.get(id);
        if (albumImage == null) throw new CustomException(400, "该照片不存在");
    }

    public void isExist(List<Integer> ids) {
        for (Integer id : ids) {
            AlbumImage albumImage = this.get(id);
            if (albumImage == null) throw new CustomException(400, "ID为" + id + "的照片不存在");
        }
    }
}