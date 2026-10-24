package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import liuyuyang.net.dto.diary.DiaryFilterDTO;
import liuyuyang.net.dto.diary.DiaryFormDTO;
import liuyuyang.net.model.Diary;
import liuyuyang.net.vo.diary.DiaryBookVO;
import liuyuyang.net.vo.diary.DiaryVO;

public interface DiaryService extends IService<Diary> {
    void addDiaryData(DiaryFormDTO diaryFormDTO);

    void editDiaryData(DiaryFormDTO diaryFormDTO);

    void delDiaryData(Integer id);

    DiaryVO getDiaryData(Integer id);

    Page<DiaryVO> getDiaryList(DiaryFilterDTO diaryFilterDTO);

    /**
     * 供已解锁的访客翻阅：不传年月时返回最近有日记的月份。
     */
    DiaryBookVO getDiaryBook(Integer year, Integer month);
}
