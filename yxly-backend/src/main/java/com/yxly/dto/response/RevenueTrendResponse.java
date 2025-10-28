package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收入趋势响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收入趋势响应")
public class RevenueTrendResponse {

    @Schema(description = "日期列表，格式：yyyy-MM-dd")
    private List<String> dates;

    @Schema(description = "对应日期的收入，单位：元")
    private List<BigDecimal> revenues;
}


