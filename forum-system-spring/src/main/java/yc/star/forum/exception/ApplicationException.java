package yc.star.forum.exception;


import yc.star.forum.common.AppResult;

/**
 * 自定义异常
 */
public class ApplicationException extends RuntimeException{

    protected AppResult errorResult;

    // 指定状态码，异常描述
    public ApplicationException(AppResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }

    public AppResult getErrorResult() {
        return errorResult;
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
