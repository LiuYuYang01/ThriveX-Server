package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.assistant.AssistantFilterDTO;
import liuyuyang.net.dto.assistant.AssistantFormDTO;
import liuyuyang.net.model.Assistant;
import liuyuyang.net.vo.assistant.AssistantVO;

import java.util.List;

public interface AssistantService extends IService<Assistant> {
    void addAssistantData(AssistantFormDTO assistantFormDTO);

    void delAssistantData(Integer id);

    void batchDelAssistantData(List<Integer> ids);

    void editAssistantData(AssistantFormDTO assistantFormDTO);

    AssistantVO getAssistantData(Integer id);

    Page<AssistantVO> getAssistantList(AssistantFilterDTO assistantFilterDTO);

    void selectDefaultAssistant(Integer id);

}
