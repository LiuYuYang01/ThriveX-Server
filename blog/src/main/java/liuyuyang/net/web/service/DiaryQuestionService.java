package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.spring.service.IService;
import liuyuyang.net.dto.diary.DiaryQuestionFormDTO;
import liuyuyang.net.model.DiaryQuestion;
import liuyuyang.net.vo.diary.DiaryQuestionVO;
import liuyuyang.net.vo.diary.DiaryUnlockVO;

import java.util.List;

public interface DiaryQuestionService extends IService<DiaryQuestion> {
    /** 前台可见：只有问题 */
    List<DiaryQuestionVO> getQuestionListForReader();

    /** 管理端可见：附带创建时间 */
    List<DiaryQuestionVO> getQuestionListForAdmin();

    void addQuestion(DiaryQuestionFormDTO diaryQuestionFormDTO);

    void editQuestion(DiaryQuestionFormDTO diaryQuestionFormDTO);

    void delQuestion(Integer id);

    /**
     * 校验密语答案，通过则签发访客阅读令牌。
     */
    DiaryUnlockVO verifyAndIssueToken(Integer questionId, String answer);
}
