package com.yxly.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现申请请求DTO
 */
@Data
public class WithdrawApplyRequest {
    
    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 提现方式
     */
    @NotBlank(message = "提现方式不能为空")
    private String withdrawMethod;
    
    /**
     * 收款账户信息
     */
    private String accountInfo;
    
    /**
     * 用户备注
     */
    private String userRemark;
}
