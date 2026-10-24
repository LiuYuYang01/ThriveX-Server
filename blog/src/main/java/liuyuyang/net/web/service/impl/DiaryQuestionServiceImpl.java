package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.DiaryTokenUtils;
import liuyuyang.net.core.utils.PasswordUtils;
import liuyuyang.net.dto.diary.DiaryQuestionFormDTO;
import liuyuyang.net.model.DiaryQuestion;
import liuyuyang.net.vo.diary.DiaryQuestionVO;
import liuyuyang.net.vo.diary.DiaryUnlockVO;
import liuyuyang.net.web.mapper.DiaryQuestionMapper;
import liuyuyang.net.web.service.DiaryQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class DiaryQuestionServiceImpl extends ServiceImpl<DiaryQuestionMapper, DiaryQuestion> implements DiaryQuestionService {
    /** 问题不存在时也要做一次等长校验，避免通过响应时间枚举问题 ID */
    private static final String TIMING_EQUIVALENT_HASH = PasswordUtils.encode("thrivex-diary-timing-equalizer");

    @Resource
    private DiaryQuestionMapper diaryQuestionMapper;
    @Resource
    private DiaryTokenUtils diaryTokenUtils;

    private DiaryQuestionVO toVO(DiaryQuestion question, boolean withCreateTime) {
        DiaryQuestionVO vo = new DiaryQuestionVO();
        vo.setId(question.getId());
        vo.setQuestion(question.getQuestion());
        if (withCreateTime) {
            vo.setCreateTime(question.getCreateTime());
        }
        return vo;
    }

    @Override
    public List<DiaryQuestionVO> getQuestionListForReader() {
        return diaryQuestionMapper.selectList(new QueryWrapper<DiaryQuestion>().orderByAsc("id"))
                .stream().map(question -> toVO(question, false)).toList();
    }

    @Override
    public List<DiaryQuestionVO> getQuestionListForAdmin() {
        return diaryQuestionMapper.selectList(new QueryWrapper<DiaryQuestion>().orderByDesc("id"))
                .stream().map(question -> toVO(question, true)).toList();
    }

    @Override
    public void addQuestion(DiaryQuestionFormDTO formDTO) {
        String question = trimText(formDTO.getQuestion());
        String answer = normalizeAnswer(formDTO.getAnswer());
        if (question.isEmpty()) {
            throw new CustomException("问题不能为空");
        }
        if (answer.isEmpty()) {
            throw new CustomException("答案不能为空");
        }
        if (existsSameQuestion(question, null)) {
            throw new CustomException("这个问题已经存在了");
        }

        DiaryQuestion entity = new DiaryQuestion();
        entity.setQuestion(question);
        entity.setAnswerHash(PasswordUtils.encode(answer));
        entity.setCreateTime(System.currentTimeMillis());
        diaryQuestionMapper.insert(entity);
    }

    @Override
    public void editQuestion(DiaryQuestionFormDTO formDTO) {
        DiaryQuestion exists = diaryQuestionMapper.selectById(formDTO.getId());
        if (exists == null) {
            throw new CustomException("编辑失败：该密语不存在");
        }
        String question = trimText(formDTO.getQuestion());
        if (question.isEmpty()) {
            throw new CustomException("问题不能为空");
        }
        if (existsSameQuestion(question, exists.getId())) {
            throw new CustomException("这个问题已经存在了");
        }

        DiaryQuestion update = new DiaryQuestion();
        update.setId(exists.getId());
        update.setQuestion(question);
        String answer = normalizeAnswer(formDTO.getAnswer());
        if (!answer.isEmpty()) {
            update.setAnswerHash(PasswordUtils.encode(answer));
        }
        diaryQuestionMapper.updateById(update);
    }

    @Override
    public void delQuestion(Integer id) {
        DiaryQuestion exists = diaryQuestionMapper.selectById(id);
        if (exists == null) {
            throw new CustomException("删除失败：该密语不存在");
        }
        diaryQuestionMapper.deleteById(id);
    }

    @Override
    public DiaryUnlockVO verifyAndIssueToken(Integer questionId, String answer) {
        List<DiaryQuestion> questions = diaryQuestionMapper.selectList(new QueryWrapper<DiaryQuestion>().orderByAsc("id"));
        if (questions.isEmpty()) {
            throw new CustomException(409, "这本日记还没有留下密语，请让主理人先在后台设置");
        }

        String normalized = normalizeAnswer(answer);
        DiaryQuestion target = questions.stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElse(null);

        boolean matched;
        if (target == null || target.getAnswerHash() == null) {
            PasswordUtils.matches(normalized, TIMING_EQUIVALENT_HASH);
            matched = false;
        } else {
            matched = PasswordUtils.matches(normalized, target.getAnswerHash());
        }
        if (!matched) {
            throw new CustomException(401, "不是这句，再想一想。");
        }
        return new DiaryUnlockVO(diaryTokenUtils.issueToken(), diaryTokenUtils.getMaxAge());
    }

    private boolean existsSameQuestion(String question, Integer excludeId) {
        QueryWrapper<DiaryQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question", question);
        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }
        return diaryQuestionMapper.selectCount(queryWrapper) > 0;
    }

    private static String trimText(String text) {
        return text == null ? "" : text.trim();
    }

    /** 中文答案常被误贴全角空格，前后空白一律忽略 */
    private static String normalizeAnswer(String answer) {
        if (answer == null) {
            return "";
        }
        return answer.replaceAll("^[\\s\\u3000]+", "").replaceAll("[\\s\\u3000]+$", "");
    }
}
