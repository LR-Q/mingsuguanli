package com.yxly.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 充值审核请求DTO
 */
@Data
public class RechargeAuditRequest {
    
    /**
     * 充值记录ID
     */
    @NotNull(message = "充值记录ID不能为空")
    private Long id;
    
    /**
     * 审核状态：1-审核通过，2-审核拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private Integer status;
    
    /**
     * 审核备注
     */
    private String auditRemark;
}








