package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import liuyuyang.net.dto.diary.DiaryFilterDTO;
import liuyuyang.net.dto.diary.DiaryFormDTO;
import liuyuyang.net.model.Diary;
import liuyuyang.net.vo.diary.DiaryVO;

import java.util.List;

public interface DiaryService extends IService<Diary> {
    void addDiaryData(DiaryFormDTO diaryFormDTO);

    void editDiaryData(DiaryFormDTO diaryFormDTO);

    void delDiaryData(Integer id);

    DiaryVO getDiaryData(Integer id);

    Page<DiaryVO> getDiaryList(DiaryFilterDTO diaryFilterDTO);

    /**
     * 供已解锁的访客翻阅：整本按日期升序返回，一篇一页。
     */
    List<DiaryVO> getDiaryPages();
}
