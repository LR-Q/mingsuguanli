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
import java.time.LocalDateTime;

/**
 * 房间信息实体
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("room_info")
@Schema(description = "房间信息")
public class RoomInfo extends BaseEntity {
    
    @Schema(description = "房间ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "房间号")
    @TableField("room_number")
    @NotBlank(message = "房间号不能为空")
    @Size(max = 20, message = "房间号长度不能超过20个字符")
    private String roomNumber;
    
    @Schema(description = "房型ID")
    @TableField("room_type_id")
    @NotNull(message = "房型ID不能为空")
    private Long roomTypeId;
    
    @Schema(description = "楼层")
    @TableField("floor_number")
    @Min(value = 1, message = "楼层必须大于0")
    private Integer floorNumber;
    
    @Schema(description = "面积(平方米)")
    @TableField("area")
    @DecimalMin(value = "0.01", message = "面积必须大于0")
    private BigDecimal area;
    
    @Schema(description = "床型")
    @TableField("bed_type")
    @Size(max = 50, message = "床型长度不能超过50个字符")
    private String bedType;
    
    @Schema(description = "最大入住人数")
    @TableField("max_guests")
    @Min(value = 1, message = "最大入住人数必须大于0")
    private Integer maxGuests;
    
    @Schema(description = "基础价格")
    @TableField("base_price")
    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.01", message = "基础价格必须大于0")
    private BigDecimal basePrice;
    
    @Schema(description = "当前价格")
    @TableField("current_price")
    @NotNull(message = "当前价格不能为空")
    @DecimalMin(value = "0.01", message = "当前价格必须大于0")
    private BigDecimal currentPrice;
    
    @Schema(description = "房间状态(1:可用 2:占用 3:维修 4:清洁 5:停用)")
    @TableField("status")
    @NotNull(message = "房间状态不能为空")
    private Integer status;
    
    @Schema(description = "房间设施")
    @TableField("facilities")
    private String facilities;
    
    @Schema(description = "房间描述")
    @TableField("description")
    private String description;
    
    @Schema(description = "房间图片")
    @TableField("images")
    private String images;
    
    @Schema(description = "WiFi密码")
    @TableField("wifi_password")
    @Size(max = 50, message = "WiFi密码长度不能超过50个字符")
    private String wifiPassword;
    
    @Schema(description = "最后清洁时间")
    @TableField("last_clean_time")
    private LocalDateTime lastCleanTime;
}
