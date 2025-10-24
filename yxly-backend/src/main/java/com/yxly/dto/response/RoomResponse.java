package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间响应DTO
 */
@Data
@Schema(description = "房间响应DTO")
public class RoomResponse {

    @Schema(description = "房间ID", example = "1")
    private Long id;

    @Schema(description = "民宿位置ID", example = "1")
    private Long locationId;

    @Schema(description = "民宿位置名称", example = "小饭灰1号")
    private String locationName;

    @Schema(description = "房间号", example = "101")
    private String roomNumber;

    @Schema(description = "房型ID", example = "1")
    private Long roomTypeId;

    @Schema(description = "房型名称", example = "标准间")
    private String roomTypeName;

    @Schema(description = "楼层", example = "1")
    private Integer floorNumber;

    @Schema(description = "面积(平方米)", example = "25.5")
    private BigDecimal area;

    @Schema(description = "床型", example = "double")
    private String bedType;

    @Schema(description = "最大入住人数", example = "2")
    private Integer maxGuests;

    @Schema(description = "基础价格", example = "288.00")
    private BigDecimal basePrice;

    @Schema(description = "当前价格", example = "288.00")
    private BigDecimal currentPrice;

    @Schema(description = "房间状态(1:可用 2:占用 3:维修 4:清洁 5:停用)", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "可用")
    private String statusName;

    @Schema(description = "房间设施(JSON格式)", example = "[\"空调\",\"电视\",\"WiFi\"]")
    private String facilities;

    @Schema(description = "房间描述", example = "标准双人间，配备独立卫浴")
    private String description;

    @Schema(description = "房间图片(JSON格式)", example = "[\"image1.jpg\",\"image2.jpg\"]")
    private String images;

    @Schema(description = "WiFi密码", example = "room101wifi")
    private String wifiPassword;

    @Schema(description = "最后清洁时间", example = "2024-10-15T10:30:00")
    private LocalDateTime lastCleanTime;

    @Schema(description = "创建时间", example = "2024-10-15T10:30:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-10-15T10:30:00")
    private LocalDateTime updateTime;
}






