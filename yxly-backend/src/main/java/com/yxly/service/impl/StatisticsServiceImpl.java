package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxly.dto.response.DashboardStatisticsResponse;
import com.yxly.entity.BookingOrder;
import com.yxly.entity.RoomInfo;
import com.yxly.entity.SysUser;
import com.yxly.mapper.BookingOrderMapper;
import com.yxly.mapper.RoomInfoMapper;
import com.yxly.mapper.SysUserMapper;
import com.yxly.service.StatisticsService;
import com.yxly.dto.response.OccupancyTrendResponse;
import com.yxly.dto.response.RevenueTrendResponse;
import com.yxly.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 统计数据服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final RoomInfoMapper roomInfoMapper;
    private final BookingOrderMapper bookingOrderMapper;
    private final SysUserMapper sysUserMapper;
    private final SecurityUtils securityUtils;

    @Override
    public DashboardStatisticsResponse getDashboardStatistics() {
        log.info("开始获取仪表板统计数据");

        // 1. 获取房间总数（未删除的房间）
        Long totalRooms = roomInfoMapper.selectCount(
            new LambdaQueryWrapper<RoomInfo>()
                .eq(RoomInfo::getDeleted, 0)
        );
        log.info("房间总数: {}", totalRooms);

        // 2. 获取今日订单数（今日创建的订单）
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        Long todayBookings = bookingOrderMapper.selectCount(
            new LambdaQueryWrapper<BookingOrder>()
                .eq(BookingOrder::getDeleted, 0)
                .ge(BookingOrder::getCreateTime, todayStart)
                .le(BookingOrder::getCreateTime, todayEnd)
        );
        log.info("今日订单数: {}", todayBookings);

        // 3. 获取客户总数（未删除的普通用户，role_id=2）
        Long totalCustomers = sysUserMapper.selectCount(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRoleId, 2L)  // 只统计普通用户
                .eq(SysUser::getDeleted, 0)
        );
        log.info("客户总数: {}", totalCustomers);

        // 4. 获取今日收入（今日创建且已支付的订单总金额）
        List<BookingOrder> todayPaidOrders = bookingOrderMapper.selectList(
            new LambdaQueryWrapper<BookingOrder>()
                .eq(BookingOrder::getDeleted, 0)
                .ge(BookingOrder::getCreateTime, todayStart)
                .le(BookingOrder::getCreateTime, todayEnd)
                .eq(BookingOrder::getPaymentStatus, 1) // 1: 已支付
        );
        
        BigDecimal todayRevenue = todayPaidOrders.stream()
            .map(BookingOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("今日收入: {}", todayRevenue);

        // 5. 累计总收入（所有已支付订单金额）
        List<BookingOrder> allPaidOrders = bookingOrderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BookingOrder>()
                .eq(BookingOrder::getDeleted, 0)
                .eq(BookingOrder::getPaymentStatus, 1)
        );
        BigDecimal totalRevenue = allPaidOrders.stream()
            .map(BookingOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. 当前管理员账户余额（从登录用户获取，若无登录则为0）
        BigDecimal accountBalance = BigDecimal.ZERO;
        try {
            Long currentUserId = securityUtils.getCurrentUserId();
            if (currentUserId != null) {
                com.yxly.entity.SysUser currentUser = sysUserMapper.selectById(currentUserId);
                if (currentUser != null && currentUser.getBalance() != null) {
                    accountBalance = currentUser.getBalance();
                }
            }
        } catch (Exception ignore) {}

        return DashboardStatisticsResponse.builder()
            .totalRooms(totalRooms)
            .todayBookings(todayBookings)
            .totalCustomers(totalCustomers)
            .todayRevenue(todayRevenue)
            .totalRevenue(totalRevenue)
            .accountBalance(accountBalance)
            .build();
    }

    @Override
    public OccupancyTrendResponse getOccupancyTrend(int days) {
        if (days <= 0) {
            days = 7;
        }

        // 房间总数（分母）
        Long totalRooms = roomInfoMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RoomInfo>()
                .eq(RoomInfo::getDeleted, 0)
        );
        if (totalRooms == null || totalRooms == 0) {
            return OccupancyTrendResponse.builder()
                .dates(new ArrayList<>())
                .occupancyRates(new ArrayList<>())
                .build();
        }

        List<String> dates = new ArrayList<>();
        List<java.math.BigDecimal> rates = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(day, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(day, LocalTime.MAX);

            // 简化口径：当日“已入住”状态订单数量/房间总数
            Long occupied = bookingOrderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BookingOrder>()
                    .eq(BookingOrder::getDeleted, 0)
                    .eq(BookingOrder::getBookingStatus, 3) // 已入住
                    .and(q -> q
                        .between(BookingOrder::getCheckInTime, start, end)
                        .or()
                        .between(BookingOrder::getCheckInDate, day, day) // 兼容历史未写入checkInTime的数据
                    )
            );

            java.math.BigDecimal rate = new java.math.BigDecimal(occupied)
                .multiply(new java.math.BigDecimal("100"))
                .divide(new java.math.BigDecimal(totalRooms), 2, java.math.RoundingMode.HALF_UP);

            dates.add(day.toString());
            rates.add(rate);
        }

        return OccupancyTrendResponse.builder()
            .dates(dates)
            .occupancyRates(rates)
            .build();
    }

    @Override
    public RevenueTrendResponse getRevenueTrend(int days) {
        if (days <= 0) {
            days = 7;
        }

        List<String> dates = new ArrayList<>();
        List<java.math.BigDecimal> revenues = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(day, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(day, LocalTime.MAX);

            // 统计当日已支付订单的总金额
            List<BookingOrder> paidOrders = bookingOrderMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BookingOrder>()
                    .eq(BookingOrder::getDeleted, 0)
                    .eq(BookingOrder::getPaymentStatus, 1)
                    .ge(BookingOrder::getCreateTime, start)
                    .le(BookingOrder::getCreateTime, end)
            );

            java.math.BigDecimal total = paidOrders.stream()
                .map(BookingOrder::getTotalAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            dates.add(day.toString());
            revenues.add(total);
        }

        return com.yxly.dto.response.RevenueTrendResponse.builder()
            .dates(dates)
            .revenues(revenues)
            .build();
    }
}
