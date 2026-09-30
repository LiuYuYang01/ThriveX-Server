package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.record.RecordCommentFilterDTO;
import liuyuyang.net.dto.record.RecordCommentFormDTO;
import liuyuyang.net.vo.record.RecordCommentVO;

import java.util.List;
import java.util.Map;

public interface RecordCommentService {
    void addRecordCommentData(RecordCommentFormDTO recordCommentFormDTO) throws Exception;

    /** 管理端回复：需登录鉴权，落库即为审核通过状态 */
    void replyRecordCommentData(RecordCommentFormDTO recordCommentFormDTO) throws Exception;

    void delRecordCommentData(Integer id);

    void batchDelRecordCommentData(List<Integer> ids);

    void editRecordCommentData(RecordCommentFormDTO recordCommentFormDTO);

    RecordCommentVO getRecordCommentData(Integer id);

    Page<RecordCommentVO> getRecordCommentList(RecordCommentFilterDTO recordCommentFilterDTO);

    Page<RecordCommentVO> getRecordCommentListByRecordId(Integer recordId, PageDTO pageDTO);

    void auditRecordCommentData(Integer id);

    void delByRecordId(Integer recordId);

    Map<Integer, Integer> countApprovedByRecordIds(List<Integer> recordIds);
}
