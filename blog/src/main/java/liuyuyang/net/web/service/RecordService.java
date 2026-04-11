package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.record.RecordFilterDTO;
import liuyuyang.net.dto.record.RecordFormDTO;
import liuyuyang.net.model.Record;
import liuyuyang.net.vo.record.RecordVO;

public interface RecordService extends IService<Record> {
    void addRecordData(RecordFormDTO recordFormDTO);

    void delRecordData(Integer id);

    void editRecordData(RecordFormDTO recordFormDTO);

    RecordVO getRecordData(Integer id);

    Page<RecordVO> getRecordList(RecordFilterDTO recordFilterDTO);
}
