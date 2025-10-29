package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房型响应DTO
 */
@Data
@Schema(description = "房型响应DTO")
public class RoomTypeResponse {

    @Schema(description = "房型ID", example = "1")
    private Long id;

    @Schema(description = "房型名称", example = "标准间")
    private String typeName;

    @Schema(description = "房型编码", example = "standard")
    private String typeCode;

    @Schema(description = "房型描述", example = "标准双人间")
    private String description;

    @Schema(description = "基础价格", example = "288.00")
    private BigDecimal basePrice;

    @Schema(description = "最大入住人数", example = "2")
    private Integer maxGuests;

    @Schema(description = "床型", example = "double")
    private String bedType;

    @Schema(description = "面积(平方米)", example = "25.0")
    private BigDecimal area;

    @Schema(description = "标准设施(JSON格式)", example = "[\"空调\",\"电视\",\"WiFi\"]")
    private String facilities;

    @Schema(description = "房型图片(JSON格式)", example = "[\"image1.jpg\",\"image2.jpg\"]")
    private String images;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态(0:禁用 1:启用)", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2024-10-15T10:30:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-10-15T10:30:00")
    private LocalDateTime updateTime;
}











