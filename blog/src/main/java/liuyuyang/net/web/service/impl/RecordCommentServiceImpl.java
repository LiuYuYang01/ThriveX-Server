package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.record.RecordCommentFilterDTO;
import liuyuyang.net.dto.record.RecordCommentFormDTO;
import liuyuyang.net.model.Record;
import liuyuyang.net.model.RecordComment;
import liuyuyang.net.vo.record.RecordCommentVO;
import liuyuyang.net.web.mapper.RecordCommentMapper;
import liuyuyang.net.web.mapper.RecordMapper;
import liuyuyang.net.web.service.RecordCommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecordCommentServiceImpl extends ServiceImpl<RecordCommentMapper, RecordComment> implements RecordCommentService {
    @Resource
    private RecordCommentMapper recordCommentMapper;
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private CommonUtils commonUtils;

    @Override
    public void addRecordCommentData(RecordCommentFormDTO recordCommentFormDTO) {
        Record record = recordMapper.selectById(recordCommentFormDTO.getRecordId());
        if (record == null) {
            throw new CustomException("该说说不存在");
        }

        if (recordCommentFormDTO.getCommentId() != null && recordCommentFormDTO.getCommentId() != 0) {
            RecordComment parent = recordCommentMapper.selectById(recordCommentFormDTO.getCommentId());
            if (parent == null) {
                throw new CustomException("被回复的评论不存在");
            }
            if (!Objects.equals(parent.getRecordId(), recordCommentFormDTO.getRecordId())) {
                throw new CustomException("评论与说说不匹配");
            }
        }

        RecordComment comment = new RecordComment();
        BeanUtils.copyProperties(recordCommentFormDTO, comment);
        if (comment.getStatus() == null) {
            comment.setStatus(0);
        }
        recordCommentMapper.insert(comment);
    }

    @Override
    public void delRecordCommentData(Integer id) {
        RecordComment data = recordCommentMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该评论不存在");
        }
        recordCommentMapper.deleteById(id);
    }

    @Override
    public void batchDelRecordCommentData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }

    @Override
    public void editRecordCommentData(RecordCommentFormDTO recordCommentFormDTO) {
        RecordComment comment = new RecordComment();
        BeanUtils.copyProperties(recordCommentFormDTO, comment);
        recordCommentMapper.updateById(comment);
    }

    @Override
    public RecordCommentVO getRecordCommentData(Integer id) {
        RecordComment data = recordCommentMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该评论不存在");
        }

        QueryWrapper<RecordComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_id", data.getRecordId());
        List<RecordComment> list = recordCommentMapper.selectList(queryWrapper);

        RecordCommentVO vo = toRecordCommentVO(data);
        vo.setRecordContent(recordContentOf(data.getRecordId()));
        vo.setChildren(buildTwoLevelCommentTree(list).stream()
                .filter(item -> Objects.equals(item.getId(), data.getId()))
                .findFirst()
                .map(RecordCommentVO::getChildren)
                .orElse(new ArrayList<>()));
        return vo;
    }

    @Override
    public Page<RecordCommentVO> getRecordCommentList(RecordCommentFilterDTO recordCommentFilterDTO) {
        List<RecordComment> flat = queryFlatComments(recordCommentFilterDTO);
        List<RecordCommentVO> vos = flat.stream().map(comment -> {
            RecordCommentVO vo = toRecordCommentVO(comment);
            vo.setRecordContent(recordContentOf(comment.getRecordId()));
            if (comment.getCommentId() != null && comment.getCommentId() != 0) {
                RecordComment parent = recordCommentMapper.selectById(comment.getCommentId());
                if (parent != null) {
                    vo.setReplyName(parent.getName());
                }
            }
            return vo;
        }).collect(Collectors.toList());
        return commonUtils.paginate(recordCommentFilterDTO, vos);
    }

    @Override
    public Page<RecordCommentVO> getRecordCommentListByRecordId(Integer recordId, PageDTO pageDTO) {
        Record record = recordMapper.selectById(recordId);
        if (record == null) {
            throw new CustomException("该说说不存在");
        }

        QueryWrapper<RecordComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_id", recordId);
        queryWrapper.eq("status", 1);
        queryWrapper.orderByAsc("create_time");

        List<RecordComment> list = recordCommentMapper.selectList(queryWrapper);
        List<RecordCommentVO> vos = buildTwoLevelCommentTree(list);
        return commonUtils.paginate(pageDTO, vos);
    }

    @Override
    public void auditRecordCommentData(Integer id) {
        RecordComment data = recordCommentMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该评论不存在");
        }
        data.setStatus(1);
        recordCommentMapper.updateById(data);
    }

    @Override
    public void delByRecordId(Integer recordId) {
        QueryWrapper<RecordComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_id", recordId);
        recordCommentMapper.delete(queryWrapper);
    }

    private List<RecordComment> queryFlatComments(RecordCommentFilterDTO recordCommentFilterDTO) {
        QueryWrapper<RecordComment> queryWrapper = commonUtils.queryWrapperDateFilter(recordCommentFilterDTO);

        if (recordCommentFilterDTO.getStatus() != null) {
            queryWrapper.eq("status", recordCommentFilterDTO.getStatus());
        }

        if (recordCommentFilterDTO.getRecordId() != null) {
            queryWrapper.eq("record_id", recordCommentFilterDTO.getRecordId());
        }

        if (recordCommentFilterDTO.getContent() != null && !recordCommentFilterDTO.getContent().trim().isEmpty()) {
            queryWrapper.like("content", "%" + recordCommentFilterDTO.getContent().trim() + "%");
        }

        queryWrapper.orderByDesc("create_time");
        return recordCommentMapper.selectList(queryWrapper);
    }

    private String recordContentOf(Integer recordId) {
        Record record = recordMapper.selectById(recordId);
        if (record == null || record.getContent() == null) {
            return null;
        }
        String content = record.getContent().trim();
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    private RecordCommentVO toRecordCommentVO(RecordComment comment) {
        if (comment == null) {
            return null;
        }
        RecordCommentVO vo = new RecordCommentVO();
        BeanUtils.copyProperties(comment, vo);
        return vo;
    }

    /**
     * 构建最多两级的评论树：一级评论 + 扁平化的二级回复（类似朋友圈/抖音）
     */
    private List<RecordCommentVO> buildTwoLevelCommentTree(List<RecordComment> flat) {
        Map<Integer, RecordComment> idMap = new HashMap<>();
        for (RecordComment comment : flat) {
            idMap.put(comment.getId(), comment);
        }

        List<RecordCommentVO> roots = flat.stream()
                .filter(comment -> comment.getCommentId() == null || comment.getCommentId() == 0)
                .sorted(Comparator.comparing(RecordComment::getCreateTime))
                .map(this::toRecordCommentVO)
                .collect(Collectors.toList());

        for (RecordCommentVO root : roots) {
            List<RecordCommentVO> replies = flat.stream()
                    .filter(comment -> comment.getCommentId() != null && comment.getCommentId() != 0)
                    .filter(comment -> Objects.equals(findRootId(comment, idMap), root.getId()))
                    .sorted(Comparator.comparing(RecordComment::getCreateTime))
                    .map(comment -> {
                        RecordCommentVO vo = toRecordCommentVO(comment);
                        RecordComment parent = idMap.get(comment.getCommentId());
                        if (parent != null) {
                            vo.setReplyName(parent.getName());
                        }
                        return vo;
                    })
                    .collect(Collectors.toList());
            root.setChildren(replies);
        }

        return roots;
    }

    private Integer findRootId(RecordComment comment, Map<Integer, RecordComment> idMap) {
        RecordComment current = comment;
        while (current.getCommentId() != null && current.getCommentId() != 0) {
            RecordComment parent = idMap.get(current.getCommentId());
            if (parent == null) {
                return null;
            }
            current = parent;
        }
        return current.getId();
    }
}
