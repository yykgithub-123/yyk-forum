package yc.star.forum.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppResult<T> {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private long code;  // 状态码
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String message;  // 描述消息
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private T data;  // 返回的具体数据

    public AppResult(long code, String message) {
        this(code,message,null);
    }

    public AppResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 操作成功
     */

    public static AppResult success() {
        return new AppResult(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage());
    }

    public static AppResult success(String message) {
        return new AppResult(ResultCode.SUCCESS.getCode(),message);
    }

    public static <T> AppResult<T> success(T data) {
        return new AppResult<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    public static <T> AppResult<T> success(String message,T data) {
        return new AppResult<>(ResultCode.SUCCESS.getCode(),message,data);
    }

    /**
     * 操作失败
     */

    public static AppResult failed() {
        return new AppResult(ResultCode.FAILED.getCode(),ResultCode.FAILED.getMessage());
    }

    public static AppResult failed(String message) {
        return new AppResult(ResultCode.FAILED.getCode(),message);
    }

    public static AppResult failed(ResultCode resultCode) {
        return new AppResult(resultCode.getCode(),resultCode.getMessage());
    }

}
