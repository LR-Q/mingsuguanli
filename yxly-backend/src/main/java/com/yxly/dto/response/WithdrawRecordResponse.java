package com.yxly.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录响应DTO
 */
@Data
public class WithdrawRecordResponse {
    
    /**
     * 提现记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 提现金额
     */
    private BigDecimal amount;
    
    /**
     * 提现方式
     */
    private String withdrawMethod;
    
    /**
     * 收款账户信息
     */
    private String accountInfo;
    
    /**
     * 申请状态：0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 审核人用户名
     */
    private String auditorName;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 用户备注
     */
    private String userRemark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}


