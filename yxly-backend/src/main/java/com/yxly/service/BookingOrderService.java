package com.yxly.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.dto.request.BookingCreateRequest;
import com.yxly.dto.response.BookingOrderResponse;

/**
 * 订单服务接口
 */
public interface BookingOrderService {
    
    /**
     * 创建订单
     */
    Long createBooking(Long userId, BookingCreateRequest request);
    
    /**
     * 获取用户订单列表（分页）
     */
    IPage<BookingOrderResponse> getUserBookingPage(Long userId, Long current, Long size, Integer status);
    
    /**
     * 获取所有订单列表（分页，管理员端）
     */
    IPage<BookingOrderResponse> getAllBookingPage(Long current, Long size, String orderNo, String customerName, String roomNumber, Integer status);
    
    /**
     * 获取订单详情（用户端）
     */
    BookingOrderResponse getBookingById(Long id, Long userId);
    
    /**
     * 获取订单详情（管理员端）
     */
    BookingOrderResponse getBookingDetailById(Long id);
    
    /**
     * 取消订单（用户端）
     */
    void cancelBooking(Long id, Long userId, String reason);
    
    /**
     * 取消订单（管理员端）
     */
    void adminCancelBooking(Long id, String reason);
    
    /**
     * 确认订单（管理员端）
     */
    void adminConfirmBooking(Long id);
    
    /**
     * 办理入住（管理员端）
     */
    void checkIn(Long id);
    
    /**
     * 办理退房（管理员端）
     */
    void checkOut(Long id);
}
