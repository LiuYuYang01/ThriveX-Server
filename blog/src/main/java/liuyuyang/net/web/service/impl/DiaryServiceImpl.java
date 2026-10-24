package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.CommonUtils;
import liuyuyang.net.dto.diary.DiaryFilterDTO;
import liuyuyang.net.dto.diary.DiaryFormDTO;
import liuyuyang.net.model.Diary;
import liuyuyang.net.vo.diary.DiaryBookVO;
import liuyuyang.net.vo.diary.DiaryMonthVO;
import liuyuyang.net.vo.diary.DiaryVO;
import liuyuyang.net.web.mapper.DiaryMapper;
import liuyuyang.net.web.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class DiaryServiceImpl extends ServiceImpl<DiaryMapper, Diary> implements DiaryService {
    @Resource
    private DiaryMapper diaryMapper;
    @Resource
    private ObjectMapper objectMapper;

    private DiaryVO toVO(Diary diary) {
        DiaryVO vo = new DiaryVO();
        BeanUtils.copyProperties(diary, vo, "diaryDate", "images");
        vo.setDiaryDate(diary.getDiaryDate() == null ? null : diary.getDiaryDate().toString());
        vo.setImages(parseImages(diary.getImages()));
        return vo;
    }

    private List<String> parseImages(String images) {
        if (images == null || images.isBlank()) {
            return new ArrayList<>();
        }
        try {
            List<String> list = objectMapper.readValue(images, new TypeReference<List<String>>() {
            });
            return list == null ? new ArrayList<>() : list.stream().filter(url -> url != null && !url.isBlank()).toList();
        } catch (Exception e) {
            log.warn("日记图片数据解析失败，已按空处理");
            return new ArrayList<>();
        }
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

    /** 标题留空时存 null，避免正文页出现空标题占位 */
    private void normalize(Diary diary) {
        if (diary.getTitle() != null) {
            String title = diary.getTitle().trim();
            diary.setTitle(title.isEmpty() ? null : title);
        }
        if (diary.getImages() == null || diary.getImages().isBlank()) {
            diary.setImages("[]");
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
    public DiaryBookVO getDiaryBook(Integer year, Integer month) {
        List<Diary> dated = diaryMapper.selectList(new QueryWrapper<Diary>().select("id", "diary_date").orderByDesc("diary_date"));

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Diary diary : dated) {
            if (diary.getDiaryDate() == null) {
                continue;
            }
            counts.merge(YearMonth.from(diary.getDiaryDate()).toString(), 1, Integer::sum);
        }

        List<DiaryMonthVO> months = new ArrayList<>();
        counts.forEach((key, count) -> {
            YearMonth ym = YearMonth.parse(key);
            months.add(new DiaryMonthVO(ym.getYear(), ym.getMonthValue(), count));
        });

        DiaryBookVO book = new DiaryBookVO();
        book.setTotal(dated.size());
        book.setMonths(months);

        YearMonth target;
        if (year != null && month != null && month >= 1 && month <= 12 && year > 1970 && year <= 2999) {
            target = YearMonth.of(year, month);
        } else if (!months.isEmpty()) {
            DiaryMonthVO latest = months.get(0);
            target = YearMonth.of(latest.getYear(), latest.getMonth());
        } else {
            target = YearMonth.now();
        }

        book.setYear(target.getYear());
        book.setMonth(target.getMonthValue());

        List<Diary> entries = diaryMapper.selectList(new QueryWrapper<Diary>()
                .ge("diary_date", target.atDay(1))
                .le("diary_date", target.atEndOfMonth())
                .orderByAsc("diary_date")
                .orderByAsc("id"));
        book.setEntries(entries.stream().map(this::toVO).toList());
        return book;
    }
}
