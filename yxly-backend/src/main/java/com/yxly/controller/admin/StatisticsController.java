package com.yxly.controller.admin;

import com.yxly.common.Result;
import com.yxly.dto.response.DashboardStatisticsResponse;
import com.yxly.dto.response.OccupancyTrendResponse;
import com.yxly.dto.response.RevenueTrendResponse;
import com.yxly.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "统计数据管理", description = "统计数据相关接口")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "获取仪表板统计数据", description = "获取房间总数、今日订单、客户总数、今日收入等统计数据")
    @GetMapping("/dashboard")
    public Result<DashboardStatisticsResponse> getDashboardStatistics() {
        try {
            log.info("获取仪表板统计数据");
            DashboardStatisticsResponse statistics = statisticsService.getDashboardStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取仪表板统计数据失败", e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取入住率趋势", description = "返回近N天的入住率趋势，默认7天")
    @GetMapping("/occupancy-trend")
    public Result<OccupancyTrendResponse> getOccupancyTrend(@org.springframework.web.bind.annotation.RequestParam(value = "days", required = false, defaultValue = "7") Integer days) {
        try {
            log.info("获取入住率趋势: days={}", days);
            OccupancyTrendResponse resp = statisticsService.getOccupancyTrend(days != null ? days : 7);
            return Result.success(resp);
        } catch (Exception e) {
            log.error("获取入住率趋势失败", e);
            return Result.error("获取入住率趋势失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取收入趋势", description = "返回近N天的收入趋势，默认7天")
    @GetMapping("/revenue-trend")
    public Result<RevenueTrendResponse> getRevenueTrend(@org.springframework.web.bind.annotation.RequestParam(value = "days", required = false, defaultValue = "7") Integer days) {
        try {
            log.info("获取收入趋势: days={}", days);
            RevenueTrendResponse resp = statisticsService.getRevenueTrend(days != null ? days : 7);
            return Result.success(resp);
        } catch (Exception e) {
            log.error("获取收入趋势失败", e);
            return Result.error("获取收入趋势失败: " + e.getMessage());
        }
    }
}
