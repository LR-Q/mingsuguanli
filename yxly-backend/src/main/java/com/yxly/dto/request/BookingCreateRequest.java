package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建订单请求DTO
 */
@Data
@Schema(description = "创建订单请求")
public class BookingCreateRequest {
    
    @Schema(description = "房间ID")
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
    
    @Schema(description = "入住日期")
    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;
    
    @Schema(description = "退房日期")
    @NotNull(message = "退房日期不能为空")
    private LocalDate checkOutDate;
    
    @Schema(description = "入住人数")
    @NotNull(message = "入住人数不能为空")
    private Integer guestsCount;
    
    @Schema(description = "联系人")
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    
    @Schema(description = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String contactPhone;
    
    @Schema(description = "特殊要求")
    private String specialRequests;
}
