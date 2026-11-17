package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.diary.DiaryFilterDTO;
import liuyuyang.net.dto.diary.DiaryFormDTO;
import liuyuyang.net.model.Diary;
import liuyuyang.net.vo.diary.DiaryArchiveVO;
import liuyuyang.net.vo.diary.DiaryVO;
import liuyuyang.net.web.mapper.DiaryMapper;
import liuyuyang.net.web.service.DiaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DiaryServiceImpl extends ServiceImpl<DiaryMapper, Diary> implements DiaryService {
    private static final String[] CN_MONTH_NAMES = {
            "", "一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"
    };

    @Resource
    private DiaryMapper diaryMapper;

    private DiaryVO toVO(Diary diary) {
        DiaryVO vo = new DiaryVO();
        BeanUtils.copyProperties(diary, vo, "diaryDate");
        vo.setDiaryDate(diary.getDiaryDate() == null ? null : diary.getDiaryDate().toString());
        return vo;
    }

    @Override
    public void addDiaryData(DiaryFormDTO diaryFormDTO) {
        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryFormDTO, diary);
        normalize(diary);
        if (diary.getCreateTime() == null) {
            diary.setCreateTime(System.currentTimeMillis());
        }
        diaryMapper.insert(diary);
    }

    @Override
    public void editDiaryData(DiaryFormDTO diaryFormDTO) {
        Diary exists = diaryMapper.selectById(diaryFormDTO.getId());
        if (exists == null) {
            throw new CustomException("编辑失败：该日记不存在");
        }
        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryFormDTO, diary);
        normalize(diary);
        diary.setUpdateTime(System.currentTimeMillis());
        diaryMapper.updateById(diary);
    }

    /** 标题留空时存 null，避免正文页出现空标题占位；同时按日记日期回填归档年月日 */
    private void normalize(Diary diary) {
        if (diary.getDiaryDate() != null) {
            diary.setDiaryYear(diary.getDiaryDate().getYear());
            diary.setDiaryMonth(diary.getDiaryDate().getMonthValue());
            diary.setDiaryDay(diary.getDiaryDate().getDayOfMonth());
        }
        if (diary.getTitle() != null) {
            String title = diary.getTitle().trim();
            diary.setTitle(title.isEmpty() ? null : title);
        }
    }

    @Override
    public void delDiaryData(Integer id) {
        Diary exists = diaryMapper.selectById(id);
        if (exists == null) {
            throw new CustomException("删除失败：该日记不存在");
        }
        diaryMapper.deleteById(id);
    }

    @Override
    public DiaryVO getDiaryData(Integer id) {
        Diary diary = diaryMapper.selectById(id);
        if (diary == null) {
            throw new CustomException("该日记不存在");
        }
        return toVO(diary);
    }

    private QueryWrapper<Diary> buildQueryWrapper(DiaryFilterDTO filterDTO) {
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("diary_date").orderByDesc("id");

        String keyword = filterDTO.getKeyword() == null ? "" : filterDTO.getKeyword().trim();
        if (!keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like("title", keyword).or().like("content", keyword));
        }
        // 日记按「日子」筛选，而不是按写入时间
        if (isDateText(filterDTO.getStartDate())) {
            queryWrapper.ge("diary_date", filterDTO.getStartDate());
        }
        if (isDateText(filterDTO.getEndDate())) {
            queryWrapper.le("diary_date", filterDTO.getEndDate());
        }
        // 归档目录筛选：年 / 月逐级收窄
        if (filterDTO.getYear() != null) {
            queryWrapper.eq("diary_year", filterDTO.getYear());
            if (filterDTO.getMonth() != null) {
                queryWrapper.eq("diary_month", filterDTO.getMonth());
            }
        }
        return queryWrapper;
    }

    private boolean isDateText(String value) {
        if (value == null || !value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Page<DiaryVO> getDiaryList(DiaryFilterDTO diaryFilterDTO) {
        QueryWrapper<Diary> queryWrapper = buildQueryWrapper(diaryFilterDTO);

        Page<Diary> page = CommonUtils.buildPage(diaryFilterDTO);
        List<Diary> raw;
        Page<DiaryVO> result;
        if (page == null) {
            raw = diaryMapper.selectList(queryWrapper);
            result = new Page<>(1, raw.size());
            result.setTotal(raw.size());
        } else {
            Page<Diary> entityPage = diaryMapper.selectPage(page, queryWrapper);
            raw = entityPage.getRecords();
            result = new Page<>(entityPage.getCurrent(), entityPage.getSize());
            result.setTotal(entityPage.getTotal());
        }
        result.setRecords(raw.stream().map(this::toVO).toList());
        return result;
    }

    @Override
    public List<DiaryArchiveVO> getDiaryArchive() {
        List<Diary> entries = diaryMapper.selectList(new QueryWrapper<Diary>()
                .orderByDesc("diary_date")
                .orderByDesc("id"));

        // 按写入顺序（新→旧）分组，年 / 月自然也是倒序
        Map<Integer, Map<Integer, Long>> tree = new LinkedHashMap<>();
        for (Diary diary : entries) {
            LocalDate date = diary.getDiaryDate();
            if (date == null) {
                continue;
            }
            tree.computeIfAbsent(date.getYear(), y -> new LinkedHashMap<>())
                    .merge(date.getMonthValue(), 1L, Long::sum);
        }

        List<DiaryArchiveVO> result = new ArrayList<>();
        tree.forEach((year, months) -> {
            DiaryArchiveVO yearVO = new DiaryArchiveVO();
            yearVO.setYear(year);
            long yearTotal = 0;
            List<DiaryArchiveVO.Month> monthVOs = new ArrayList<>();
            for (Map.Entry<Integer, Long> monthEntry : months.entrySet()) {
                DiaryArchiveVO.Month monthVO = new DiaryArchiveVO.Month();
                monthVO.setMonth(monthEntry.getKey());
                monthVO.setName(CN_MONTH_NAMES[monthEntry.getKey()]);
                monthVO.setTotal(monthEntry.getValue());
                monthVOs.add(monthVO);
                yearTotal += monthEntry.getValue();
            }
            yearVO.setTotal(yearTotal);
            yearVO.setMonths(monthVOs);
            result.add(yearVO);
        });
        return result;
    }

    @Override
    public List<DiaryVO> getDiaryPages() {
        List<Diary> entries = diaryMapper.selectList(new QueryWrapper<Diary>()
                .orderByAsc("diary_date")
                .orderByAsc("id"));
        return entries.stream().map(this::toVO).toList();
    }
}
