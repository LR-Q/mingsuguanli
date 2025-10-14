package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yxly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预订订单实体
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("booking_order")
@Schema(description = "预订订单")
public class BookingOrder extends BaseEntity {
    
    @Schema(description = "订单ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "订单号")
    @TableField("order_no")
    @NotBlank(message = "订单号不能为空")
    @Size(max = 32, message = "订单号长度不能超过32个字符")
    private String orderNo;
    
    @Schema(description = "客户ID")
    @TableField("customer_id")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    
    @Schema(description = "房间ID")
    @TableField("room_id")
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
    
    @Schema(description = "入住日期")
    @TableField("check_in_date")
    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;
    
    @Schema(description = "退房日期")
    @TableField("check_out_date")
    @NotNull(message = "退房日期不能为空")
    private LocalDate checkOutDate;
    
    @Schema(description = "入住天数")
    @TableField("nights")
    @NotNull(message = "入住天数不能为空")
    @Min(value = 1, message = "入住天数必须大于0")
    private Integer nights;
    
    @Schema(description = "入住人数")
    @TableField("guests_count")
    @Min(value = 1, message = "入住人数必须大于0")
    private Integer guestsCount;
    
    @Schema(description = "房间单价")
    @TableField("room_price")
    @NotNull(message = "房间单价不能为空")
    @DecimalMin(value = "0.01", message = "房间单价必须大于0")
    private BigDecimal roomPrice;
    
    @Schema(description = "订单总金额")
    @TableField("total_amount")
    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
    private BigDecimal totalAmount;
    
    @Schema(description = "已付金额")
    @TableField("paid_amount")
    private BigDecimal paidAmount;
    
    @Schema(description = "预订状态(1:待确认 2:已确认 3:已入住 4:已退房 5:已取消)")
    @TableField("booking_status")
    private Integer bookingStatus;
    
    @Schema(description = "支付状态(0:未支付 1:已支付 2:部分支付 3:已退款)")
    @TableField("payment_status")
    private Integer paymentStatus;
    
    @Schema(description = "预订来源")
    @TableField("booking_source")
    @Size(max = 20, message = "预订来源长度不能超过20个字符")
    private String bookingSource;
    
    @Schema(description = "特殊要求")
    @TableField("special_requests")
    private String specialRequests;
    
    @Schema(description = "联系电话")
    @TableField("contact_phone")
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String contactPhone;
    
    @Schema(description = "联系人")
    @TableField("contact_name")
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactName;
    
    @Schema(description = "实际入住时间")
    @TableField("check_in_time")
    private LocalDateTime checkInTime;
    
    @Schema(description = "实际退房时间")
    @TableField("check_out_time")
    private LocalDateTime checkOutTime;
    
    @Schema(description = "操作员ID")
    @TableField("operator_id")
    private Long operatorId;
    
    @Schema(description = "取消原因")
    @TableField("cancel_reason")
    @Size(max = 200, message = "取消原因长度不能超过200个字符")
    private String cancelReason;
    
    @Schema(description = "取消时间")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;
}
