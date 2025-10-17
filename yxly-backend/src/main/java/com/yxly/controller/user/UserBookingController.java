package com.yxly.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.Result;
import com.yxly.dto.request.BookingCreateRequest;
import com.yxly.dto.response.BookingOrderResponse;
import com.yxly.service.BookingOrderService;
import com.yxly.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户订单Controller
 */
@Tag(name = "用户订单管理", description = "用户订单相关接口")
@RestController
@RequestMapping("/user/bookings")
@RequiredArgsConstructor
@Slf4j
public class UserBookingController {
    
    private final BookingOrderService bookingOrderService;
    private final SecurityUtils securityUtils;
    
    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Long> createBooking(@Validated @RequestBody BookingCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        Long orderId = bookingOrderService.createBooking(userId, request);
        return Result.success(orderId);
    }
    
    @Operation(summary = "获取我的订单列表")
    @GetMapping("/my")
    public Result<IPage<BookingOrderResponse>> getMyBookings(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status
    ) {
        Long userId = securityUtils.getCurrentUserId();
        IPage<BookingOrderResponse> page = bookingOrderService.getUserBookingPage(userId, current, size, status);
        return Result.success(page);
    }
    
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<BookingOrderResponse> getBookingById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        BookingOrderResponse booking = bookingOrderService.getBookingById(id, userId);
        return Result.success(booking);
    }
    
    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        Long userId = securityUtils.getCurrentUserId();
        bookingOrderService.cancelBooking(id, userId, reason);
        return Result.success();
    }
}
