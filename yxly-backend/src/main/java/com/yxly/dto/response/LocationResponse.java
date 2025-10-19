package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 位置信息响应DTO
 */
@Data
@Schema(description = "位置信息响应")
public class LocationResponse {

    @Schema(description = "位置ID")
    private Long id;

    @Schema(description = "位置名称")
    private String name;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "位置描述")
    private String description;

    @Schema(description = "地图类型")
    private String mapType;

    @Schema(description = "是否启用(0:禁用 1:启用)")
    private Integer isActive;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
