package liuyuyang.net.core.execption;

import com.qiniu.common.QiniuException;
import liuyuyang.net.core.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String GENERIC_MESSAGE = "系统繁忙，请稍后再试";

    @Resource
    private Environment environment;

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> methodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = resolveBindingMessage(e.getBindingResult().getFieldError());
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public Result<Object> bindException(BindException e) {
        String message = resolveBindingMessage(e.getBindingResult().getFieldError());
        log.warn("参数绑定校验失败: {}", message);
        return Result.error(400, message);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> constraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数校验失败");
        log.warn("参数约束校验失败: {}", message);
        return Result.error(400, message);
    }

    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public Result<Object> customException(CustomException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(QiniuException.class)
    public Result<Object> qiniuException(QiniuException e) {
        log.error("七牛云 OSS 异常: code={}, error={}", e.code(), e.error(), e);
        if (exposeExceptionDetails()) {
            return Result.error(e.code(), e.error());
        }
        return Result.error(GENERIC_MESSAGE);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result<Object> exception(Exception e) {
        log.error("未捕获的异常", e);
        if (exposeExceptionDetails()) {
            return Result.error(e.getMessage());
        }
        return Result.error(GENERIC_MESSAGE);
    }

    private String resolveBindingMessage(FieldError fieldError) {
        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            return fieldError.getDefaultMessage();
        }
        return "参数校验失败";
    }

    private boolean exposeExceptionDetails() {
        for (String profile : environment.getActiveProfiles()) {
            if ("dev".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}
