package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.dto.milestone.MilestoneFilterDTO;
import liuyuyang.net.dto.milestone.MilestoneFormDTO;
import liuyuyang.net.model.Milestone;
import liuyuyang.net.vo.milestone.MilestoneVO;
import liuyuyang.net.web.mapper.MilestoneMapper;
import liuyuyang.net.web.service.MilestoneService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MilestoneServiceImpl extends ServiceImpl<MilestoneMapper, Milestone> implements MilestoneService {
 @Resource
 private MilestoneMapper milestoneMapper;

 @Override
 public void addMilestoneData(MilestoneFormDTO milestoneFormDTO) {
 Milestone milestone = new Milestone();
 BeanUtils.copyProperties(milestoneFormDTO, milestone);
 save(milestone);
 }

 @Override
 public void delMilestoneData(Integer id) {
 Milestone data = milestoneMapper.selectById(id);
 if (data == null) {
 throw new CustomException("该里程碑不存在");
 }
 milestoneMapper.deleteById(id);
 }

 @Override
 public void batchDelMilestoneData(List<Integer> ids) {
 if (ids == null || ids.isEmpty()) {
 return;
 }
 removeByIds(ids);
 }

 @Override
 public void editMilestoneData(MilestoneFormDTO milestoneFormDTO) {
 Milestone milestone = new Milestone();
 BeanUtils.copyProperties(milestoneFormDTO, milestone);
 updateById(milestone);
 }

 @Override
 public MilestoneVO getMilestoneData(Integer id) {
 Milestone data = milestoneMapper.selectById(id);
 if (data == null) {
 throw new CustomException("该里程碑不存在");
 }
 return toVO(data);
 }

 @Override
 public Page<MilestoneVO> getMilestoneList(MilestoneFilterDTO milestoneFilterDTO) {
 LambdaQueryWrapper<Milestone> queryWrapper = new LambdaQueryWrapper<Milestone>()
 .orderByAsc(Milestone::getEventDate)
 .orderByAsc(Milestone::getId);

 if (milestoneFilterDTO != null) {
 if (StringUtils.hasText(milestoneFilterDTO.getTitle())) {
 queryWrapper.like(Milestone::getTitle, milestoneFilterDTO.getTitle());
 }
 if (StringUtils.hasText(milestoneFilterDTO.getYear())) {
 int year = Integer.parseInt(milestoneFilterDTO.getYear());
 long start = LocalDate.of(year,1,1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
 long end = LocalDate.of(year +1,1,1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
 queryWrapper.ge(Milestone::getEventDate, start).lt(Milestone::getEventDate, end);
 }
 }

 if (milestoneFilterDTO == null || milestoneFilterDTO.getPageNum() == null || milestoneFilterDTO.getPageSize() == null) {
 List<Milestone> data = list(queryWrapper);
 Page<MilestoneVO> result = new Page<>(1, data.size());
 result.setRecords(data.stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
 result.setTotal((long) data.size());
 return result;
 }

 if (milestoneFilterDTO.getPageNum() <=0 || milestoneFilterDTO.getPageSize() <=0) {
 throw new CustomException("分页参数 page/size 必须大于0");
 }

 Page<Milestone> page = new Page<>(milestoneFilterDTO.getPageNum(), milestoneFilterDTO.getPageSize());
 milestoneMapper.selectPage(page, queryWrapper);
 Page<MilestoneVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
 voPage.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toCollection(ArrayList::new)));
 return voPage;
 }

 private MilestoneVO toVO(Milestone milestone) {
 MilestoneVO vo = new MilestoneVO();
 BeanUtils.copyProperties(milestone, vo);
 return vo;
 }
}
