package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 民宿位置简单信息VO
 * 用于下拉选择等场景
 *
 * @author yxly
 * @since 2024-01-01
 */
@Data
@Schema(description = "民宿位置简单信息")
public class LocationSimpleVO {
    
    @Schema(description = "位置ID")
    private Long id;
    
    @Schema(description = "位置名称（民宿名称）")
    private String name;
    
    @Schema(description = "详细地址")
    private String address;
    
    @Schema(description = "联系电话")
    private String contactPhone;
    
    @Schema(description = "是否启用")
    private Integer isActive;
}
