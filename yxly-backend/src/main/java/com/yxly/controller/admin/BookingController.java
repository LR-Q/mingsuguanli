package com.yxly.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.Result;
import com.yxly.dto.response.BookingOrderResponse;
import com.yxly.service.BookingOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员订单Controller
 */
@Tag(name = "管理员订单管理", description = "管理员订单相关接口")
@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingOrderService bookingOrderService;
    
    @Operation(summary = "获取所有订单列表")
    @GetMapping
    public Result<IPage<BookingOrderResponse>> getAllBookings(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "客户姓名") @RequestParam(required = false) String customerName,
            @Parameter(description = "房间号") @RequestParam(required = false) String roomNumber,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status
    ) {
        try {
            log.info("管理员查询订单列表: page={}, size={}, orderNo={}, customerName={}, roomNumber={}, status={}", 
                    page, size, orderNo, customerName, roomNumber, status);
            
            IPage<BookingOrderResponse> bookingPage = bookingOrderService.getAllBookingPage(
                    page, size, orderNo, customerName, roomNumber, status
            );
            
            log.info("查询成功，返回 {} 条记录，共 {} 条", bookingPage.getRecords().size(), bookingPage.getTotal());
            return Result.success(bookingPage);
        } catch (Exception e) {
            log.error("查询订单列表失败", e);
            return Result.error("查询订单列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "管理员取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        try {
            log.info("管理员取消订单: id={}, reason={}", id, reason);
            bookingOrderService.adminCancelBooking(id, reason);
            return Result.success();
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Operation(summary = "管理员确认订单")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmBooking(@PathVariable Long id) {
        try {
            log.info("管理员确认订单: id={}", id);
            bookingOrderService.adminConfirmBooking(id);
            return Result.success();
        } catch (Exception e) {
            log.error("确认订单失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Operation(summary = "办理入住")
    @PostMapping("/{id}/check-in")
    public Result<Void> checkIn(@PathVariable Long id) {
        try {
            log.info("办理入住: id={}", id);
            bookingOrderService.checkIn(id);
            return Result.success();
        } catch (Exception e) {
            log.error("办理入住失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Operation(summary = "办理退房")
    @PostMapping("/{id}/check-out")
    public Result<Void> checkOut(@PathVariable Long id) {
        try {
            log.info("办理退房: id={}", id);
            bookingOrderService.checkOut(id);
            return Result.success();
        } catch (Exception e) {
            log.error("办理退房失败", e);
            return Result.error(e.getMessage());
        }
    }
}
