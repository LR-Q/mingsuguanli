package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
@Data
@Schema(description = "订单响应")
public class BookingOrderResponse {
    
    @Schema(description = "订单ID")
    private Long id;
    
    @Schema(description = "订单号")
    private String orderNo;
    
    @Schema(description = "房间ID")
    private Long roomId;
    
    @Schema(description = "房间名称")
    private String roomName;
    
    @Schema(description = "房间号")
    private String roomNumber;
    
    @Schema(description = "房间类型")
    private String roomType;

    @Schema(description = "房间首图")
    private String roomImage;

    @Schema(description = "入住日期")
    private LocalDate checkInDate;
    
    @Schema(description = "退房日期")
    private LocalDate checkOutDate;
    
    @Schema(description = "入住天数")
    private Integer nights;
    
    @Schema(description = "入住人数")
    private Integer guestsCount;
    
    @Schema(description = "房间单价")
    private BigDecimal roomPrice;
    
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;
    
    @Schema(description = "已付金额")
    private BigDecimal paidAmount;
    
    @Schema(description = "预订状态")
    private Integer bookingStatus;
    
    @Schema(description = "预订状态名称")
    private String bookingStatusName;
    
    @Schema(description = "支付状态")
    private Integer paymentStatus;
    
    @Schema(description = "支付状态名称")
    private String paymentStatusName;
    
    @Schema(description = "特殊要求")
    private String specialRequests;
    
    @Schema(description = "联系电话")
    private String contactPhone;
    
    @Schema(description = "联系人")
    private String contactName;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "取消原因")
    private String cancelReason;
    
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;
}
