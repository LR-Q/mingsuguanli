package com.yxly.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Getter
public enum BookingStatus {
    
    PENDING(1, "待确认"),
    CONFIRMED(2, "已确认"),
    CHECKED_IN(3, "已入住"),
    CHECKED_OUT(4, "已退房"),
    CANCELLED(5, "已取消");
    
    private final Integer code;
    private final String desc;
    
    BookingStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static BookingStatus getByCode(Integer code) {
        for (BookingStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
