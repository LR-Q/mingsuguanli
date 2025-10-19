package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 位置信息更新请求DTO
 */
@Data
@Schema(description = "位置信息更新请求")
public class LocationUpdateRequest {

    @Schema(description = "位置名称", required = true, example = "悦鑫乐怡民宿")
    @NotBlank(message = "位置名称不能为空")
    private String name;

    @Schema(description = "详细地址", required = true, example = "北京市朝阳区XX街道XX号")
    @NotBlank(message = "详细地址不能为空")
    private String address;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;

    @Schema(description = "区县", example = "朝阳区")
    private String district;

    @Schema(description = "经度", required = true, example = "116.4073963")
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @Schema(description = "纬度", required = true, example = "39.9041999")
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "位置描述", example = "位于市中心，交通便利")
    private String description;

    @Schema(description = "地图类型", example = "baidu")
    private String mapType;

    @Schema(description = "是否启用(0:禁用 1:启用)", example = "1")
    private Integer isActive;
}
