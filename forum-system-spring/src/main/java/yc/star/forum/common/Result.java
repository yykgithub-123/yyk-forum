package yc.star.forum.common;

import lombok.Data;

/**
 * 统一API响应结果封装
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    /**
     * 成功返回结果
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 成功返回结果
     * @param data 返回的数据
     * @param message 返回的消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回结果
     * @param code 错误码
     * @param message 错误信息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败返回结果
     * @param message 错误信息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }
} 