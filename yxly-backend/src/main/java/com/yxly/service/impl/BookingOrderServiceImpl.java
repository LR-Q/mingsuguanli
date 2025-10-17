package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.common.ResultCode;
import com.yxly.dto.request.BookingCreateRequest;
import com.yxly.dto.response.BookingOrderResponse;
import com.yxly.entity.BookingOrder;
import com.yxly.entity.FinancialRecord;
import com.yxly.entity.RoomInfo;
import com.yxly.entity.RoomType;
import com.yxly.entity.SysUser;
import com.yxly.enums.BookingStatus;
import com.yxly.exception.BusinessException;
import com.yxly.mapper.BookingOrderMapper;
import com.yxly.mapper.FinancialRecordMapper;
import com.yxly.mapper.RoomInfoMapper;
import com.yxly.mapper.RoomTypeMapper;
import com.yxly.mapper.SysUserMapper;
import com.yxly.service.BookingOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingOrderServiceImpl implements BookingOrderService {
    
    private final BookingOrderMapper bookingOrderMapper;
    private final RoomInfoMapper roomInfoMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final SysUserMapper sysUserMapper;
    private final FinancialRecordMapper financialRecordMapper;
    
    // 状态映射
    private static final Map<Integer, String> BOOKING_STATUS_MAP = new HashMap<>();
    private static final Map<Integer, String> PAYMENT_STATUS_MAP = new HashMap<>();
    
    static {
        BOOKING_STATUS_MAP.put(1, "待确认");
        BOOKING_STATUS_MAP.put(2, "已确认");
        BOOKING_STATUS_MAP.put(3, "已入住");
        BOOKING_STATUS_MAP.put(4, "已完成");
        BOOKING_STATUS_MAP.put(5, "已取消");
        BOOKING_STATUS_MAP.put(6, "被取消");
        
        PAYMENT_STATUS_MAP.put(0, "未支付");
        PAYMENT_STATUS_MAP.put(1, "已支付");
        PAYMENT_STATUS_MAP.put(2, "部分支付");
        PAYMENT_STATUS_MAP.put(3, "已退款");
    }
    
    @Override
    @Transactional
    public Long createBooking(Long userId, BookingCreateRequest request) {
        log.info("用户创建订单: userId={}, roomId={}", userId, request.getRoomId());
        
        // 验证房间是否存在
        RoomInfo room = roomInfoMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "房间不存在");
        }
        
        // 验证日期
        if (request.getCheckOutDate().isBefore(request.getCheckInDate()) || 
            request.getCheckOutDate().isEqual(request.getCheckInDate())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "退房日期必须晚于入住日期");
        }
        
        // 计算入住天数
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入住天数必须大于0");
        }
        
        // 计算总金额
        BigDecimal roomPrice = room.getCurrentPrice() != null ? room.getCurrentPrice() : room.getBasePrice();
        BigDecimal totalAmount = roomPrice.multiply(BigDecimal.valueOf(nights));
        
        // 检查用户余额是否充足
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        BigDecimal userBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        if (userBalance.compareTo(totalAmount) < 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, 
                String.format("余额不足，当前余额：￥%.2f，需要支付：￥%.2f", userBalance, totalAmount));
        }
        
        // 扣除用户余额
        BigDecimal newBalance = userBalance.subtract(totalAmount);
        user.setBalance(newBalance);
        int updateResult = sysUserMapper.updateById(user);
        if (updateResult <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "扣款失败");
        }
        
        log.info("用户余额扣款成功: userId={}, 扣款金额={}, 原余额={}, 新余额={}", 
                userId, totalAmount, userBalance, newBalance);
        
        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 先创建订单
        BookingOrder order = new BookingOrder();
        order.setOrderNo(orderNo);
        order.setCustomerId(userId);
        order.setRoomId(request.getRoomId());
        order.setCheckInDate(request.getCheckInDate());
        order.setCheckOutDate(request.getCheckOutDate());
        order.setNights((int) nights);
        order.setGuestsCount(request.getGuestsCount());
        order.setRoomPrice(roomPrice);
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(totalAmount); // 已全额支付
        order.setBookingStatus(BookingStatus.PENDING.getCode()); // 待确认
        order.setPaymentStatus(1); // 已支付
        order.setBookingSource("网站");
        order.setSpecialRequests(request.getSpecialRequests());
        order.setContactPhone(request.getContactPhone());
        order.setContactName(request.getContactName());
        
        int result = bookingOrderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "订单创建失败");
        }
        
        // 注意：下单时不创建交易记录，等管理员确认后才创建消费记录
        // 这样可以避免一个订单有多条交易记录，保持订单号统一
        
        log.info("订单创建成功: orderId={}, orderNo={}, 支付金额={}，等待管理员确认后创建消费记录", order.getId(), orderNo, totalAmount);
        return order.getId();
    }
    
    @Override
    public IPage<BookingOrderResponse> getUserBookingPage(Long userId, Long current, Long size, Integer status) {
        log.info("分页查询用户订单: userId={}, current={}, size={}, status={}", userId, current, size, status);
        
        Page<BookingOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getCustomerId, userId);
        
        if (status != null && status > 0) {
            wrapper.eq(BookingOrder::getBookingStatus, status);
        }
        
        wrapper.orderByDesc(BookingOrder::getCreateTime);
        
        IPage<BookingOrder> orderPage = bookingOrderMapper.selectPage(page, wrapper);
        
        // 转换为响应DTO
        return convertToResponsePage(orderPage);
    }
    
    @Override
    public IPage<BookingOrderResponse> getAllBookingPage(Long current, Long size, String orderNo, String customerName, String roomNumber, Integer status) {
        log.info("分页查询所有订单: current={}, size={}, orderNo={}, customerName={}, roomNumber={}, status={}", 
                current, size, orderNo, customerName, roomNumber, status);
        
        Page<BookingOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 订单号搜索
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            wrapper.like(BookingOrder::getOrderNo, orderNo.trim());
        }
        
        // 联系人姓名搜索
        if (customerName != null && !customerName.trim().isEmpty()) {
            wrapper.like(BookingOrder::getContactName, customerName.trim());
        }
        
        // 房间号搜索 - 需要关联查询
        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            // 先查找房间ID
            LambdaQueryWrapper<RoomInfo> roomWrapper = new LambdaQueryWrapper<>();
            roomWrapper.like(RoomInfo::getRoomNumber, roomNumber.trim());
            roomWrapper.select(RoomInfo::getId);
            java.util.List<RoomInfo> rooms = roomInfoMapper.selectList(roomWrapper);
            if (!rooms.isEmpty()) {
                java.util.List<Long> roomIds = rooms.stream().map(RoomInfo::getId).collect(java.util.stream.Collectors.toList());
                wrapper.in(BookingOrder::getRoomId, roomIds);
            } else {
                // 没有找到匹配的房间，返回空结果
                return new Page<>(current, size);
            }
        }
        
        // 状态筛选
        if (status != null && status > 0) {
            wrapper.eq(BookingOrder::getBookingStatus, status);
        }
        
        wrapper.orderByDesc(BookingOrder::getCreateTime);
        
        IPage<BookingOrder> orderPage = bookingOrderMapper.selectPage(page, wrapper);
        
        // 转换为响应DTO
        return convertToResponsePage(orderPage);
    }
    
    /**
     * 将订单分页转换为响应DTO分页
     */
    private IPage<BookingOrderResponse> convertToResponsePage(IPage<BookingOrder> orderPage) {
        try {
            log.debug("开始转换订单分页数据，共 {} 条记录", orderPage.getRecords().size());
            
            IPage<BookingOrderResponse> responsePage = orderPage.convert(order -> {
                try {
                    BookingOrderResponse response = new BookingOrderResponse();
                    BeanUtils.copyProperties(order, response);
                    
                    log.debug("处理订单: id={}, orderNo={}, roomId={}", order.getId(), order.getOrderNo(), order.getRoomId());
                    
                    // 获取房间信息
                    if (order.getRoomId() != null) {
                        RoomInfo room = roomInfoMapper.selectById(order.getRoomId());
                        if (room != null) {
                            response.setRoomNumber(room.getRoomNumber());
                            
                            // 获取房间类型
                            if (room.getRoomTypeId() != null) {
                                RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
                                if (roomType != null) {
                                    response.setRoomType(roomType.getTypeName());
                                    response.setRoomName(room.getRoomNumber() + "号房 - " + roomType.getTypeName());
                                } else {
                                    response.setRoomName(room.getRoomNumber() + "号房");
                                }
                            } else {
                                response.setRoomName(room.getRoomNumber() + "号房");
                            }
                        } else {
                            log.warn("订单{}关联的房间{}不存在", order.getId(), order.getRoomId());
                            response.setRoomNumber("未知");
                            response.setRoomName("未知房间");
                        }
                    }
                    
                    // 设置状态名称
                    response.setBookingStatusName(BOOKING_STATUS_MAP.get(order.getBookingStatus()));
                    response.setPaymentStatusName(PAYMENT_STATUS_MAP.get(order.getPaymentStatus()));
                    
                    return response;
                } catch (Exception e) {
                    log.error("转换订单数据失败: orderId={}", order.getId(), e);
                    throw new RuntimeException("转换订单数据失败", e);
                }
            });
            
            log.debug("订单分页数据转换完成");
            return responsePage;
        } catch (Exception e) {
            log.error("转换订单分页数据失败", e);
            throw e;
        }
    }
    
    @Override
    public BookingOrderResponse getBookingById(Long id, Long userId) {
        log.info("获取订单详情: id={}, userId={}", id, userId);
        
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "订单不存在");
        }
        
        // 验证订单所属用户
        if (!order.getCustomerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该订单");
        }
        
        BookingOrderResponse response = new BookingOrderResponse();
        BeanUtils.copyProperties(order, response);
        
        // 获取房间信息
        RoomInfo room = roomInfoMapper.selectById(order.getRoomId());
        if (room != null) {
            response.setRoomNumber(room.getRoomNumber());
            
            if (room.getRoomTypeId() != null) {
                RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
                if (roomType != null) {
                    response.setRoomType(roomType.getTypeName());
                    response.setRoomName(room.getRoomNumber() + "号房 - " + roomType.getTypeName());
                }
            }
        }
        
        // 设置状态名称
        response.setBookingStatusName(BOOKING_STATUS_MAP.get(order.getBookingStatus()));
        response.setPaymentStatusName(PAYMENT_STATUS_MAP.get(order.getPaymentStatus()));
        
        return response;
    }
    
    @Override
    @Transactional
    public void cancelBooking(Long id, Long userId, String reason) {
        log.info("取消订单: id={}, userId={}, reason={}", id, userId, reason);
        
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "订单不存在");
        }
        
        // 验证订单所属用户
        if (!order.getCustomerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作该订单");
        }
        
        // 只有待确认的订单可以取消
        if (!BookingStatus.PENDING.getCode().equals(order.getBookingStatus())) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "只有待确认的订单可以取消");
        }
        
        // 如果已支付，退款到用户余额
        if (order.getPaymentStatus() != null && order.getPaymentStatus() == 1) {
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null) {
                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                BigDecimal refundAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
                BigDecimal newBalance = currentBalance.add(refundAmount);
                
                user.setBalance(newBalance);
                int updateResult = sysUserMapper.updateById(user);
                if (updateResult <= 0) {
                    throw new BusinessException(ResultCode.OPERATION_FAILED, "退款失败");
                }
                
                log.info("订单退款成功: userId={}, 退款金额={}, 原余额={}, 新余额={}", 
                        userId, refundAmount, currentBalance, newBalance);
                
                // 尝试创建退款交易记录（失败不影响退款）
                try {
                    FinancialRecord refundRecord = new FinancialRecord();
                    refundRecord.setRecordNo(order.getOrderNo() + "-R"); // 订单号+R后缀表示退款
                    refundRecord.setType(1); // 1:收入
                    refundRecord.setCategory("REFUND"); // 退款
                    refundRecord.setAmount(refundAmount);
                    refundRecord.setRelatedOrderId(order.getId());
                    refundRecord.setDescription("用户取消订单退款 - 订单号:" + order.getOrderNo());
                    refundRecord.setPaymentMethod("余额退款");
                    refundRecord.setOperatorId(userId);
                    refundRecord.setRecordDate(LocalDate.now());
                    refundRecord.setStatus(1); // 1:有效
                    financialRecordMapper.insert(refundRecord);
                    log.info("退款交易记录创建成功: recordNo={}", refundRecord.getRecordNo());
                } catch (Exception e) {
                    log.error("退款交易记录创建失败，但退款已成功: orderId={}, error={}", order.getId(), e.getMessage());
                }
                
                // 更新订单支付状态为已退款
                order.setPaymentStatus(3);
            }
        }
        
        // 更新订单状态
        order.setBookingStatus(BookingStatus.CANCELLED.getCode());
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        
        int result = bookingOrderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "订单取消失败");
        }
        
        log.info("订单取消成功: orderId={}", id);
    }
    
    @Override
    @Transactional
    public void adminCancelBooking(Long id, String reason) {
        log.info("管理员取消订单: id={}, reason={}", id, reason);
        
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "订单不存在");
        }
        
        // 只有待确认的订单可以取消
        if (!BookingStatus.PENDING.getCode().equals(order.getBookingStatus())) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "只有待确认的订单可以取消");
        }
        
        // 如果已支付，退款到用户余额
        if (order.getPaymentStatus() != null && order.getPaymentStatus() == 1) {
            SysUser user = sysUserMapper.selectById(order.getCustomerId());
            if (user != null) {
                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                BigDecimal refundAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
                BigDecimal newBalance = currentBalance.add(refundAmount);
                
                user.setBalance(newBalance);
                int updateResult = sysUserMapper.updateById(user);
                if (updateResult <= 0) {
                    throw new BusinessException(ResultCode.OPERATION_FAILED, "退款失败");
                }
                
                log.info("管理员取消订单退款成功: userId={}, 退款金额={}, 原余额={}, 新余额={}", 
                        user.getId(), refundAmount, currentBalance, newBalance);
                
                // 尝试创建退款交易记录（失败不影响退款）
                try {
                    FinancialRecord refundRecord = new FinancialRecord();
                    refundRecord.setRecordNo(order.getOrderNo() + "-R"); // 订单号+R后缀表示退款
                    refundRecord.setType(1); // 1:收入
                    refundRecord.setCategory("REFUND"); // 退款
                    refundRecord.setAmount(refundAmount);
                    refundRecord.setRelatedOrderId(order.getId());
                    refundRecord.setDescription("管理员取消订单退款 - 订单号:" + order.getOrderNo());
                    refundRecord.setPaymentMethod("余额退款");
                    refundRecord.setOperatorId(user.getId());
                    refundRecord.setRecordDate(LocalDate.now());
                    refundRecord.setStatus(1); // 1:有效
                    financialRecordMapper.insert(refundRecord);
                    log.info("退款交易记录创建成功: recordNo={}", refundRecord.getRecordNo());
                } catch (Exception e) {
                    log.error("退款交易记录创建失败，但退款已成功: orderId={}, error={}", order.getId(), e.getMessage());
                }
                
                // 更新订单支付状态为已退款
                order.setPaymentStatus(3);
            }
        }
        
        // 更新订单状态为"被取消"
        order.setBookingStatus(BookingStatus.CANCELLED_BY_ADMIN.getCode());
        order.setCancelReason(reason != null ? reason : "管理员取消");
        order.setCancelTime(LocalDateTime.now());
        
        int result = bookingOrderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "订单取消失败");
        }
        
        log.info("管理员取消订单成功: orderId={}, 状态设置为被取消(6)", id);
    }
    
    @Override
    @Transactional
    public void adminConfirmBooking(Long id) {
        log.info("管理员确认订单: id={}", id);
        
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "订单不存在");
        }
        
        // 只有待确认的订单可以确认
        if (!BookingStatus.PENDING.getCode().equals(order.getBookingStatus())) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "只有待确认的订单可以确认");
        }
        
        // 更新订单状态为已确认
        order.setBookingStatus(BookingStatus.CONFIRMED.getCode());
        
        int result = bookingOrderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "订单确认失败");
        }
        
        // 创建消费交易记录
        try {
            SysUser user = sysUserMapper.selectById(order.getCustomerId());
            if (user != null) {
                FinancialRecord consumptionRecord = new FinancialRecord();
                consumptionRecord.setRecordNo(order.getOrderNo()); // 使用订单号作为交易单号
                consumptionRecord.setType(2); // 2:支出
                consumptionRecord.setCategory("CONSUMPTION"); // 消费
                consumptionRecord.setAmount(order.getTotalAmount());
                consumptionRecord.setRelatedOrderId(order.getId());
                consumptionRecord.setDescription("订房消费 - 订单号:" + order.getOrderNo() + " | 房间:" + 
                                           (order.getRoomId() != null ? order.getRoomId() : "未知"));
                consumptionRecord.setPaymentMethod("余额支付");
                consumptionRecord.setOperatorId(user.getId());
                consumptionRecord.setRecordDate(LocalDate.now());
                consumptionRecord.setStatus(1); // 1:有效
                
                financialRecordMapper.insert(consumptionRecord);
                log.info("订单确认成功，已创建消费交易记录: orderId={}, amount={}", id, order.getTotalAmount());
            }
        } catch (Exception e) {
            log.error("创建消费交易记录失败，但订单确认成功: orderId={}, error={}", id, e.getMessage(), e);
            // 交易记录创建失败不影响订单确认
        }
        
        log.info("管理员确认订单成功: orderId={}", id);
    }
    
    @Override
    @Transactional
    public void checkIn(Long id) {
        log.info("办理入住: id={}", id);
        
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "订单不存在");
        }
        
        // 只有已确认的订单可以办理入住
        if (!BookingStatus.CONFIRMED.getCode().equals(order.getBookingStatus())) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "只有已确认的订单可以办理入住");
        }
        
        // 更新订单状态为已入住
        order.setBookingStatus(BookingStatus.CHECKED_IN.getCode());
        
        int result = bookingOrderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "办理入住失败");
        }
        
        log.info("办理入住成功: orderId={}", id);
    }
    
    @Override
    @Transactional
    public void checkOut(Long id) {
        log.info("办理退房: id={}", id);
        
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "订单不存在");
        }
        
        // 只有已入住的订单可以办理退房
        if (!BookingStatus.CHECKED_IN.getCode().equals(order.getBookingStatus())) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "只有已入住的订单可以办理退房");
        }
        
        // 更新订单状态为已退房（已完成）
        order.setBookingStatus(BookingStatus.CHECKED_OUT.getCode());
        
        int result = bookingOrderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "办理退房失败");
        }
        
        log.info("办理退房成功: orderId={}，订单已完成", id);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "YX" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
}
