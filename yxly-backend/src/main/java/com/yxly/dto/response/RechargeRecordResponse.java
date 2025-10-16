package com.yxly.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录响应DTO
 */
@Data
public class RechargeRecordResponse {
    
    /**
     * 主键ID
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
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 充值方式
     */
    private String paymentMethod;
    
    /**
     * 支付凭证
     */
    private String paymentProof;
    
    /**
     * 申请状态
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 审核人ID
     */
    private Long auditorId;
    
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
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
