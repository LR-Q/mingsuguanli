package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 民宿简单信息VO
 * 用于下拉选择等场景
 *
 * @author yxly
 * @since 2024-01-01
 */
@Data
@Schema(description = "民宿简单信息")
public class MerchantSimpleVO {
    
    @Schema(description = "民宿ID")
    private Long id;
    
    @Schema(description = "民宿名称")
    private String merchantName;
    
    @Schema(description = "民宿编码")
    private String merchantCode;
    
    @Schema(description = "详细地址")
    private String address;
    
    @Schema(description = "房源数量")
    private Integer roomCount;
}
