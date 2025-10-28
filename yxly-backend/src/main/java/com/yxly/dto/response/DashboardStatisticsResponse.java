package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 仪表板统计数据响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表板统计数据响应DTO")
public class DashboardStatisticsResponse {

    @Schema(description = "房间总数", example = "50")
    private Long totalRooms;

    @Schema(description = "今日订单数", example = "12")
    private Long todayBookings;

    @Schema(description = "客户总数", example = "368")
    private Long totalCustomers;

    @Schema(description = "今日收入", example = "8680.00")
    private BigDecimal todayRevenue;

    @Schema(description = "总收入（累计已支付订单金额）", example = "123456.78")
    private BigDecimal totalRevenue;

    @Schema(description = "当前账户余额（当前登录管理员）", example = "998.00")
    private BigDecimal accountBalance;
}
