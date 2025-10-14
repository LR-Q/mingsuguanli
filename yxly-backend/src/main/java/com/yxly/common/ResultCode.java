package com.yxly.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Getter
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    PARAM_ERROR(422, "参数验证失败"),
    
    // 服务器错误 5xx
    INTERNAL_ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    
    // 业务错误 1xxxx
    BUSINESS_ERROR(10000, "业务处理失败"),
    
    // 用户相关错误 101xx
    USER_NOT_FOUND(10101, "用户不存在"),
    USER_ALREADY_EXISTS(10102, "用户已存在"),
    PASSWORD_ERROR(10103, "密码错误"),
    TOKEN_EXPIRED(10104, "登录已过期"),
    TOKEN_INVALID(10105, "无效的令牌"),
    USER_DISABLED(10106, "用户已被禁用"),
    LOGIN_FAILED(10107, "登录失败"),
    EMAIL_ALREADY_EXISTS(10108, "邮箱已被注册"),
    PHONE_ALREADY_EXISTS(10109, "手机号已被注册"),
    SMS_CODE_ERROR(10111, "短信验证码错误"),
    
    // 房间相关错误 102xx
    ROOM_NOT_FOUND(10201, "房间不存在"),
    ROOM_NOT_AVAILABLE(10202, "房间不可用"),
    ROOM_ALREADY_BOOKED(10203, "房间已被预订"),
    ROOM_NUMBER_EXISTS(10204, "房间号已存在"),
    
    // 订单相关错误 103xx
    ORDER_NOT_FOUND(10301, "订单不存在"),
    ORDER_STATUS_ERROR(10302, "订单状态错误"),
    ORDER_CANNOT_CANCEL(10303, "订单无法取消"),
    ORDER_ALREADY_PAID(10304, "订单已支付"),
    
    // 支付相关错误 104xx
    PAYMENT_FAILED(10401, "支付失败"),
    INSUFFICIENT_BALANCE(10402, "余额不足"),
    PAYMENT_TIMEOUT(10403, "支付超时"),
    REFUND_FAILED(10404, "退款失败"),
    
    // 客户相关错误 105xx
    CUSTOMER_NOT_FOUND(10501, "客户不存在"),
    CUSTOMER_INFO_INCOMPLETE(10502, "客户信息不完整"),
    
    // 文件相关错误 106xx
    FILE_UPLOAD_FAILED(10601, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(10602, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(10603, "文件大小超出限制");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
