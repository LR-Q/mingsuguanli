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

/**
 * 房间类型实体
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("room_type")
@Schema(description = "房间类型")
public class RoomType extends BaseEntity {
    
    @Schema(description = "房型ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "房型名称")
    @TableField("type_name")
    @NotBlank(message = "房型名称不能为空")
    @Size(max = 50, message = "房型名称长度不能超过50个字符")
    private String typeName;
    
    @Schema(description = "房型编码")
    @TableField("type_code")
    @NotBlank(message = "房型编码不能为空")
    @Size(max = 20, message = "房型编码长度不能超过20个字符")
    private String typeCode;
    
    @Schema(description = "房型描述")
    @TableField("description")
    private String description;
    
    @Schema(description = "基础价格")
    @TableField("base_price")
    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.01", message = "基础价格必须大于0")
    private BigDecimal basePrice;
    
    @Schema(description = "最大入住人数")
    @TableField("max_guests")
    @Min(value = 1, message = "最大入住人数必须大于0")
    private Integer maxGuests;
    
    @Schema(description = "床型")
    @TableField("bed_type")
    @Size(max = 50, message = "床型长度不能超过50个字符")
    private String bedType;
    
    @Schema(description = "面积(平方米)")
    @TableField("area")
    @DecimalMin(value = "0.01", message = "面积必须大于0")
    private BigDecimal area;
    
    @Schema(description = "标准设施")
    @TableField("facilities")
    private String facilities;
    
    @Schema(description = "房型图片")
    @TableField("images")
    private String images;
    
    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;
    
    @Schema(description = "状态(0:禁用 1:启用)")
    @TableField("status")
    private Integer status;
}
