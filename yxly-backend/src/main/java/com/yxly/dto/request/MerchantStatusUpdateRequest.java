package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 商户启用/禁用请求
 */
@Data
@Schema(description = "商户启用/禁用请求")
public class MerchantStatusUpdateRequest {

    @NotNull(message = "商户ID不能为空")
    @Schema(description = "商户ID", required = true)
    private Long merchantId;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态(0:禁用 1:启用)", required = true, allowableValues = {"0", "1"})
    private Integer status;
}
