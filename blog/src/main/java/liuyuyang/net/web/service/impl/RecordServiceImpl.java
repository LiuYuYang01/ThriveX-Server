package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.record.RecordFilterDTO;
import liuyuyang.net.dto.record.RecordFormDTO;
import liuyuyang.net.model.Record;
import liuyuyang.net.vo.record.RecordVO;
import liuyuyang.net.web.mapper.RecordMapper;
import liuyuyang.net.web.service.RecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private CommonUtils commonUtils;

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

    private List<Record> queryRecordList(RecordFilterDTO recordFilterDTO) {
        QueryWrapper<Record> queryWrapper = commonUtils.queryWrapperDateFilter(recordFilterDTO);
        if (recordFilterDTO.getContent() != null && !recordFilterDTO.getContent().trim().isEmpty()) {
            queryWrapper.like("content", "%" + recordFilterDTO.getContent().trim() + "%");
        }
        return recordMapper.selectList(queryWrapper);
    }

    @Override
    public Page<RecordVO> getRecordList(RecordFilterDTO recordFilterDTO) {
        List<Record> raw = queryRecordList(recordFilterDTO);
        List<RecordVO> list = raw.stream().map(RecordServiceImpl::toRecordVO).collect(Collectors.toList());

        // 不传 page/size 则返回全部
        if (recordFilterDTO.getPageNum() == null || recordFilterDTO.getPageSize() == null) {
            Page<RecordVO> result = new Page<>(1, list.size());
            result.setRecords(new ArrayList<>(list));
            result.setTotal(list.size());
            return result;
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNum(Math.max(1, recordFilterDTO.getPageNum()));
        pageDTO.setPageSize(Math.max(1, recordFilterDTO.getPageSize()));
        return commonUtils.getPageData(pageDTO, list);
    }
}
