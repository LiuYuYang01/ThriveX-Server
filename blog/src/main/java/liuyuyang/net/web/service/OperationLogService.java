package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import liuyuyang.net.dto.operationlog.OperationLogFilterDTO;
import liuyuyang.net.model.OperationLog;
import liuyuyang.net.vo.operationlog.OperationLogVO;

import java.util.List;

public interface OperationLogService extends IService<OperationLog> {
    /**
     * 异步记录操作日志，失败不影响业务流程
     */
    void recordLog(OperationLog operationLog);

    Page<OperationLogVO> getOperationLogList(OperationLogFilterDTO filterDTO);

    void delOperationLogData(Integer id);

    void batchDelOperationLogData(List<Integer> ids);

    void clearOperationLog();
}
