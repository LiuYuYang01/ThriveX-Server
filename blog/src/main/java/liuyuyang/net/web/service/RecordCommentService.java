package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.record.RecordCommentFilterDTO;
import liuyuyang.net.dto.record.RecordCommentFormDTO;
import liuyuyang.net.vo.record.RecordCommentVO;

import java.util.List;

public interface RecordCommentService {
    void addRecordCommentData(RecordCommentFormDTO recordCommentFormDTO);

    void delRecordCommentData(Integer id);

    void batchDelRecordCommentData(List<Integer> ids);

    void editRecordCommentData(RecordCommentFormDTO recordCommentFormDTO);

    RecordCommentVO getRecordCommentData(Integer id);

    Page<RecordCommentVO> getRecordCommentList(RecordCommentFilterDTO recordCommentFilterDTO);

    Page<RecordCommentVO> getRecordCommentListByRecordId(Integer recordId, PageDTO pageDTO);

    void auditRecordCommentData(Integer id);

    void delByRecordId(Integer recordId);
}
