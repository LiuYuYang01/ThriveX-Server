package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.PageDTO;
import liuyuyang.net.dto.assistant.AssistantFilterDTO;
import liuyuyang.net.dto.assistant.AssistantFormDTO;
import liuyuyang.net.enums.assistant.AssistantDefaultEnum;
import liuyuyang.net.model.Assistant;
import liuyuyang.net.vo.assistant.AssistantVO;
import liuyuyang.net.web.mapper.AssistantMapper;
import liuyuyang.net.web.service.AssistantService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssistantServiceImpl extends ServiceImpl<AssistantMapper, Assistant> implements AssistantService {
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private AssistantMapper assistantMapper;

    @Override
    public void addAssistantData(AssistantFormDTO assistantFormDTO) {
        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(assistantFormDTO, assistant);
        assistant.setId(null);
        assistantMapper.insert(assistant);
    }

    @Override
    public void delAssistantData(Integer id) {
        Assistant data = assistantMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该助手不存在");
        }
        if (data.getIsDefault() != null && data.getIsDefault() == AssistantDefaultEnum.DEFAULT.getValue()) {
            throw new CustomException("无法删除默认助手，请更换后重试");
        }

        assistantMapper.deleteById(id);
    }

    @Override
    public void batchDelAssistantData(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<Assistant> list = assistantMapper.selectBatchIds(ids);
        if (list.size() != ids.size()) {
            throw new CustomException("有 " + (ids.size() - list.size()) + " 个助手不存在");
        }
        boolean hasDefaultAssistant = list.stream().anyMatch(item ->
                item.getIsDefault() != null && item.getIsDefault() == AssistantDefaultEnum.DEFAULT.getValue()
        );
        if (hasDefaultAssistant) {
            throw new CustomException("无法删除默认助手，请更换后重试");
        }
        removeByIds(ids);
    }

    @Override
    public void editAssistantData(AssistantFormDTO assistantFormDTO) {
        Assistant oldAssistant = assistantMapper.selectById(assistantFormDTO.getId());
        if (oldAssistant == null) {
            throw new CustomException("该助手不存在");
        }

        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(assistantFormDTO, assistant);
        if (assistant.getIsDefault() != null && assistant.getIsDefault() == AssistantDefaultEnum.DEFAULT.getValue()) {
            setDefaultAssistant(assistant);
        } else if (oldAssistant.getIsDefault() != null
                && oldAssistant.getIsDefault() == AssistantDefaultEnum.DEFAULT.getValue()) {
            // 默认助手不能被改为非默认，避免出现无默认助手
            assistant.setIsDefault(AssistantDefaultEnum.DEFAULT.getValue());
        }
        updateById(assistant);
    }

    @Override
    public AssistantVO getAssistantData(Integer id) {
        Assistant data = assistantMapper.selectById(id);
        if (data == null) {
            throw new CustomException("该助手不存在");
        }

        AssistantVO assistantVO = new AssistantVO();
        BeanUtils.copyProperties(data, assistantVO);
        return assistantVO;
    }

    @Override
    public Page<AssistantVO> getAssistantList(AssistantFilterDTO assistantFilterDTO) {
        QueryWrapper<Assistant> queryWrapper = new QueryWrapper<>();
        if (assistantFilterDTO.getName() != null && !assistantFilterDTO.getName().trim().isEmpty()) {
            queryWrapper.like("name", assistantFilterDTO.getName().trim());
        }
        queryWrapper.orderByDesc("is_default");
        queryWrapper.orderByDesc("id");

        List<AssistantVO> list = assistantMapper.selectList(queryWrapper).stream().map(item -> {
            AssistantVO assistantVO = new AssistantVO();
            BeanUtils.copyProperties(item, assistantVO);
            return assistantVO;
        }).collect(Collectors.toList());

        return commonUtils.paginate(assistantFilterDTO, list);
    }

    @Override
    public void selectDefaultAssistant(Integer id) {
        Assistant assistant = assistantMapper.selectById(id);
        if (assistant == null) {
            throw new CustomException("暂无该助手");
        }
        setDefaultAssistant(assistant);
        assistantMapper.updateById(assistant);
    }

    private void setDefaultAssistant(Assistant assistant) {
        lambdaUpdate()
                .set(Assistant::getIsDefault, AssistantDefaultEnum.NOT_DEFAULT.getValue())
                .update();
        assistant.setIsDefault(AssistantDefaultEnum.DEFAULT.getValue());
    }

}