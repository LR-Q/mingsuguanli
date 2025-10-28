package com.yxly.service;

import com.yxly.dto.response.DashboardStatisticsResponse;
import com.yxly.dto.response.OccupancyTrendResponse;
import com.yxly.dto.response.RevenueTrendResponse;

/**
 * 统计数据服务接口
 */
public interface StatisticsService {

    /**
     * 获取仪表板统计数据
     *
     * @return 统计数据
     */
    DashboardStatisticsResponse getDashboardStatistics();

    /**
     * 获取入住率趋势
     * @param days 近N天
     */
    OccupancyTrendResponse getOccupancyTrend(int days);

    /**
     * 获取收入趋势
     * @param days 近N天
     */
    RevenueTrendResponse getRevenueTrend(int days);
}
