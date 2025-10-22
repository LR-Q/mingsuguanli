package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 收藏请求DTO
 * 
 * @author yxly
 * @since 2024-10-20
 */
@Data
@Schema(description = "收藏请求")
public class FavoriteRequest {
    
    @Schema(description = "房间ID", required = true)
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
}
