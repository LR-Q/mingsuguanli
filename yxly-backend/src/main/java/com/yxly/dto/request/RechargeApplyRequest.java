package com.yxly.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 充值申请请求DTO
 */
@Data
public class RechargeApplyRequest {
    
    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 充值方式
     */
    @NotBlank(message = "充值方式不能为空")
    private String paymentMethod;
    
    /**
     * 支付凭证
     */
    @NotBlank(message = "支付凭证不能为空")
    private String paymentProof;
    
    /**
     * 用户备注
     */
    private String userRemark;
}







