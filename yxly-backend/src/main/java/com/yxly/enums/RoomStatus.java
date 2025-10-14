package com.yxly.enums;

import lombok.Getter;

/**
 * 房间状态枚举
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Getter
public enum RoomStatus {
    
    AVAILABLE(1, "可用"),
    OCCUPIED(2, "占用"),
    MAINTENANCE(3, "维修"),
    CLEANING(4, "清洁"),
    DISABLED(5, "停用");
    
    private final Integer code;
    private final String desc;
    
    RoomStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static RoomStatus getByCode(Integer code) {
        for (RoomStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
