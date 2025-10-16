package com.yxly.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提现审核请求DTO
 */
@Data
public class WithdrawAuditRequest {
    
    /**
     * 提现记录ID
     */
    @NotNull(message = "提现记录ID不能为空")
    private Long withdrawId;
    
    /**
     * 审核状态：1-通过，2-拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private Integer status;
    
    /**
     * 审核备注
     */
    private String auditRemark;
}
