package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建房间请求DTO
 */
@Data
@Schema(description = "创建房间请求DTO")
public class RoomCreateRequest {

    @Schema(description = "民宿位置ID", required = true, example = "1")
    @NotNull(message = "民宿位置ID不能为空")
    private Long locationId;

    @Schema(description = "房间号", required = true, example = "101")
    @NotBlank(message = "房间号不能为空")
    @Size(max = 20, message = "房间号长度不能超过20个字符")
    private String roomNumber;

    @Schema(description = "房型ID", required = true, example = "1")
    @NotNull(message = "房型ID不能为空")
    private Long roomTypeId;

    @Schema(description = "楼层", example = "1")
    @Min(value = 1, message = "楼层必须大于0")
    @Max(value = 50, message = "楼层不能超过50")
    private Integer floorNumber;

    @Schema(description = "面积(平方米)", example = "25.5")
    @DecimalMin(value = "10.0", message = "面积不能小于10平方米")
    @DecimalMax(value = "200.0", message = "面积不能超过200平方米")
    private BigDecimal area;

    @Schema(description = "床型（单卧室房型使用）", example = "double")
    private String bedType;

    @Schema(description = "卧室配置（多卧室房型使用，JSON格式）", example = "{\"bedrooms\":[{\"name\":\"主卧\",\"bedType\":\"大床\",\"bedCount\":1}]}")
    private String bedroomConfig;

    @Schema(description = "最大入住人数", example = "2")
    @NotNull(message = "最大入住人数不能为空")
    @Min(value = 1, message = "最大入住人数不能小于1")
    @Max(value = 10, message = "最大入住人数不能超过10")
    private Integer maxGuests;

    @Schema(description = "基础价格", required = true, example = "288.00")
    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.0", message = "基础价格不能小于0")
    private BigDecimal basePrice;

    @Schema(description = "当前价格", required = true, example = "288.00")
    @NotNull(message = "当前价格不能为空")
    @DecimalMin(value = "0.0", message = "当前价格不能小于0")
    private BigDecimal currentPrice;

    @Schema(description = "房间状态(1:可用 2:占用 3:维修 4:清洁 5:停用)", example = "1")
    @NotNull(message = "房间状态不能为空")
    @Min(value = 1, message = "房间状态值无效")
    @Max(value = 5, message = "房间状态值无效")
    private Integer status;

    @Schema(description = "房间设施(JSON格式)", example = "[\"空调\",\"电视\",\"WiFi\"]")
    private String facilities;

    @Schema(description = "房间描述", example = "标准双人间，配备独立卫浴")
    @Size(max = 500, message = "房间描述长度不能超过500个字符")
    private String description;

    @Schema(description = "房间图片(JSON格式)", example = "[\"image1.jpg\",\"image2.jpg\"]")
    private String images;

    @Schema(description = "WiFi密码", example = "room101wifi")
    @Size(max = 50, message = "WiFi密码长度不能超过50个字符")
    private String wifiPassword;
}











