package yc.star.forum.common;


import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(0, "成功"),
    ERROR_SERVICES(2000, "服务器内部错误"),
    ERROR_IS_NULL(2001, "IS NULL."),
    FAILED(1000, "操作失败"),
    FAILED_UNAUTHORIZED(1001, "未授权"),
    FAILED_PARAMS_VALIDATE(1002, "参数校验失败"),
    FAILED_FORBIDDEN(1003, "禁⽌访问"),
    FAILED_CREATE(1004, "新增失败"),
    FAILED_NOT_EXISTS(1005, "资源不存在"),
    FAILED_USER_ALTICLE_COUNT(1006,"用户更新帖子数量失败"),
    FAILED_UNLOGIN(1007,"用户未登录"),
    AILED_USER_EXISTS(1101, "⽤⼾已存在"),
    FAILED_USER_NOT_EXISTS(1102, "⽤⼾不存在"),
    FAILED_LOGIN(1103, "⽤⼾名或密码错误"),
    FAILED_USER_BANNED(1104, "您已被禁⾔, 请联系管理员, 并重新登录."),
    FAILED_TWO_PWD_NOT_SAME(1105, "两次输⼊的密码不⼀致"),
    AILED_USERNAME_EXISTS(1106, "⽤⼾名已存在，不允许修改"),
    FAILED_BOARD_ALTICLE_COUNT(1201,"板块更新帖子数量失败"),
    FAILED_BOARD_BANNED(1202,"板块状态异常"),
    FAILED_BOARD_NOT_EXISTS(1203, "板块不存在"),
    FAILED_ARTICLE_NOT_EXISTS(1301, "帖子不存在"),
    FAILED_ARTICLE_BANNED(1302,"帖子已被禁用"),
    FAILED_ARTICLE_STATE_ABNORMAL(1303,"帖子状态异常"),
    FAILED_PERMISSION(1401, "权限不足")
    ;

    // 状态码
    final long code;
    // 状态描述
    final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResultCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}