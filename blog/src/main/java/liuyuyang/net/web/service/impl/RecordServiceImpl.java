package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.record.RecordFilterDTO;
import liuyuyang.net.dto.record.RecordFormDTO;
import liuyuyang.net.model.Record;
import liuyuyang.net.vo.record.RecordVO;
import liuyuyang.net.web.mapper.RecordMapper;
import liuyuyang.net.web.service.RecordCommentService;
import liuyuyang.net.web.service.RecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private RecordCommentService recordCommentService;

    private static RecordVO toRecordVO(Record record) {
        if (record == null) {
            return null;
        }
        RecordVO vo = new RecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    @Override
    public void addRecordData(RecordFormDTO recordFormDTO) {
        Record record = new Record();
        BeanUtils.copyProperties(recordFormDTO, record);
        recordMapper.insert(record);
    }

    @Override
    public void delRecordData(Integer id) {
        Record data = recordMapper.selectById(id);
        if (data == null) {
            throw new CustomException("删除说说失败：该说说不存在");
        }
        recordCommentService.delByRecordId(id);
        recordMapper.deleteById(id);
    }

    @Override
    public void editRecordData(RecordFormDTO recordFormDTO) {
        Record record = new Record();
        BeanUtils.copyProperties(recordFormDTO, record);
        recordMapper.updateById(record);
    }

    @Override
    public RecordVO getRecordData(Integer id) {
        Record data = recordMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该说说不存在");
        }
        return toRecordVO(data);
    }

    private QueryWrapper<Record> buildRecordQueryWrapper(RecordFilterDTO recordFilterDTO) {
        // 置顶的排前面，其余按时间倒序
        QueryWrapper<Record> queryWrapper = commonUtils.queryWrapperDateFilter(recordFilterDTO, "top");
        if (recordFilterDTO.getContent() != null && !recordFilterDTO.getContent().trim().isEmpty()) {
            queryWrapper.like("content", "%" + recordFilterDTO.getContent().trim() + "%");
        }
        return queryWrapper;
    }

    @Override
    public Page<RecordVO> getRecordList(RecordFilterDTO recordFilterDTO) {
        QueryWrapper<Record> queryWrapper = buildRecordQueryWrapper(recordFilterDTO);

        Page<Record> page = CommonUtils.buildPage(recordFilterDTO);
        List<Record> raw;
        Page<RecordVO> result;
        if (page == null) {
            raw = recordMapper.selectList(queryWrapper);
            result = new Page<>(1, raw.size());
            result.setTotal(raw.size());
        } else {
            Page<Record> entityPage = recordMapper.selectPage(page, queryWrapper);
            raw = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }

        List<RecordVO> list = raw.stream().map(RecordServiceImpl::toRecordVO).collect(Collectors.toList());

        List<Integer> ids = list.stream().map(Record::getId).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Integer, Integer> commentCounts = recordCommentService.countApprovedByRecordIds(ids);
        list.forEach(vo -> vo.setCommentCount(commentCounts.getOrDefault(vo.getId(), 0)));

        result.setRecords(list);
        return result;
    }

    @Override
    public Integer incrementRecordLike(Integer id, Integer count) {
        Record data = recordMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该说说不存在");
        }

        UpdateWrapper<Record> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).setSql("like_count = like_count + " + count);
        recordMapper.update(null, updateWrapper);

        Record updated = recordMapper.selectById(id);
        return updated.getLikeCount() != null ? updated.getLikeCount() : count;
    }

    @Override
    public void setRecordTop(Integer id, Boolean top) {
        Record data = recordMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该说说不存在");
        }

        Record record = new Record();
        record.setId(id);
        record.setTop(Boolean.TRUE.equals(top));
        recordMapper.updateById(record);
    }
}
