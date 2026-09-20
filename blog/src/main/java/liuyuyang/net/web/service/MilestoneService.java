package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import liuyuyang.net.dto.milestone.MilestoneFilterDTO;
import liuyuyang.net.dto.milestone.MilestoneFormDTO;
import liuyuyang.net.model.Milestone;
import liuyuyang.net.vo.milestone.MilestoneVO;

import java.util.List;

public interface MilestoneService extends IService<Milestone> {
    void addMilestoneData(MilestoneFormDTO milestoneFormDTO);

    void delMilestoneData(Integer id);

    void batchDelMilestoneData(List<Integer> ids);

    void editMilestoneData(MilestoneFormDTO milestoneFormDTO);

    MilestoneVO getMilestoneData(Integer id);

    Page<MilestoneVO> getMilestoneList(MilestoneFilterDTO milestoneFilterDTO);
}
