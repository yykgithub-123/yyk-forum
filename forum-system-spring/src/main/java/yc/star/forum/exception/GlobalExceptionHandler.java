package yc.star.forum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.ResultCode;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public AppResult applicationExceptionHandler(ApplicationException e) {
        log.error(e.getMessage());
        if (e.getErrorResult() != null) {
            return e.getErrorResult();
        }
        return AppResult.failed(ResultCode.ERROR_SERVICES);
    }

    @ExceptionHandler(Exception.class)
    public AppResult exceptionHandler (Exception e) {
        log.error(e.getMessage());
        return AppResult.failed(ResultCode.ERROR_SERVICES);
    }

}
