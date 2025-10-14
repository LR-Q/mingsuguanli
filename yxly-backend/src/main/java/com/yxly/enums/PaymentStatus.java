package com.yxly.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Getter
public enum PaymentStatus {
    
    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    PARTIAL_PAID(2, "部分支付"),
    REFUNDED(3, "已退款");
    
    private final Integer code;
    private final String desc;
    
    PaymentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static PaymentStatus getByCode(Integer code) {
        for (PaymentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
