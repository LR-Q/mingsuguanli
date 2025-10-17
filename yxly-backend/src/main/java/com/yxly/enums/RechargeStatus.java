package com.yxly.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 充值状态枚举
 */
@Getter
@AllArgsConstructor
public enum RechargeStatus {
    
    /**
     * 待审核
     */
    PENDING(0, "待审核"),
    
    /**
     * 审核通过
     */
    APPROVED(1, "审核通过"),
    
    /**
     * 审核拒绝
     */
    REJECTED(2, "审核拒绝");
    
    private final Integer code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static RechargeStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RechargeStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
