package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量检查收藏请求DTO
 * 
 * @author yxly
 * @since 2024-10-20
 */
@Data
@Schema(description = "批量检查收藏请求")
public class BatchCheckFavoriteRequest {
    
    @Schema(description = "房间ID列表", required = true)
    @NotEmpty(message = "房间ID列表不能为空")
    private List<Long> roomIds;
}
