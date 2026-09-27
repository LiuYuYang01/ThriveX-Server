package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.operationlog.OperationLogFilterDTO;
import liuyuyang.net.model.OperationLog;
import liuyuyang.net.vo.operationlog.OperationLogVO;
import liuyuyang.net.web.mapper.OperationLogMapper;
import liuyuyang.net.web.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    @Resource
    private OperationLogMapper operationLogMapper;

    @Async
    @Override
    public void recordLog(OperationLog operationLog) {
        try {
            if (operationLog.getCreateTime() == null) {
                operationLog.setCreateTime(System.currentTimeMillis());
            }
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            // 日志记录失败仅打印告警，不影响业务主流程
            log.error("记录操作日志失败：", e);
        }
    }

    @Override
    public Page<OperationLogVO> getOperationLogList(OperationLogFilterDTO filterDTO) {
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getId);

        if (filterDTO != null) {
            queryWrapper
                    .eq(StringUtils.hasText(filterDTO.getModule()), OperationLog::getModule, filterDTO.getModule())
                    .eq(StringUtils.hasText(filterDTO.getType()), OperationLog::getType, filterDTO.getType())
                    .eq(filterDTO.getStatus() != null, OperationLog::getStatus, filterDTO.getStatus());

            if (StringUtils.hasText(filterDTO.getKeyword())) {
                String keyword = filterDTO.getKeyword();
                queryWrapper.and(wrapper -> wrapper
                        .like(OperationLog::getDescription, keyword)
                        .or().like(OperationLog::getUrl, keyword)
                        .or().like(OperationLog::getUsername, keyword)
                        .or().like(OperationLog::getIp, keyword));
            }

            if (filterDTO.getStartDate() != null && filterDTO.getEndDate() != null) {
                queryWrapper.between(OperationLog::getCreateTime, filterDTO.getStartDate(), filterDTO.getEndDate());
            } else if (filterDTO.getStartDate() != null) {
                queryWrapper.ge(OperationLog::getCreateTime, filterDTO.getStartDate());
            } else if (filterDTO.getEndDate() != null) {
                queryWrapper.le(OperationLog::getCreateTime, filterDTO.getEndDate());
            }
        }

        if (filterDTO == null || filterDTO.getPageNum() == null || filterDTO.getPageSize() == null) {
            List<OperationLog> data = list(queryWrapper);
            Page<OperationLogVO> result = new Page<>(1, data.size());
            result.setRecords(data.stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
            result.setTotal((long) data.size());
            return result;
        }

        if (filterDTO.getPageNum() <= 0 || filterDTO.getPageSize() <= 0) {
            throw new CustomException("分页参数 pageNum/pageSize 必须大于0");
        }

        Page<OperationLog> page = new Page<>(filterDTO.getPageNum(), filterDTO.getPageSize());
        operationLogMapper.selectPage(page, queryWrapper);
        Page<OperationLogVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
        return voPage;
    }

    @Override
    public void delOperationLogData(Integer id) {
        OperationLog data = operationLogMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该操作日志不存在");
        }
        operationLogMapper.deleteById(id);
    }

    @Override
    public void batchDelOperationLogData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }

    @Override
    public void clearOperationLog() {
        remove(Wrappers.emptyWrapper());
    }

    private OperationLogVO toVO(OperationLog operationLog) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(operationLog, vo);
        return vo;
    }
}
