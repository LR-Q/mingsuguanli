package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 商户审核请求DTO
 */
@Data
@Schema(description = "商户审核请求")
public class MerchantAuditRequest {
    
    @NotNull(message = "商户ID不能为空")
    @Schema(description = "商户ID", required = true)
    private Long merchantId;
    
    @NotNull(message = "审核状态不能为空")
    @Schema(description = "审核状态(1:通过 2:拒绝)", required = true)
    private Integer auditStatus;
    
    @Schema(description = "审核备注")
    private String auditRemarks;
}
