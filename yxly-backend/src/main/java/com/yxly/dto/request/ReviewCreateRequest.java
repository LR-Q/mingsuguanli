package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "创建评论请求")
public class ReviewCreateRequest {

    @Schema(description = "房间ID", required = true)
    @NotNull
    private Long roomId;

    @Schema(description = "订单ID", required = true)
    @NotNull
    private Long orderId;

    @Schema(description = "评分(1-5)", required = true)
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Schema(description = "评论内容", required = true)
    @NotBlank
    @Size(max = 500)
    private String content;

    @Schema(description = "是否匿名")
    private Boolean anonymous = false;

    @Schema(description = "是否为追评(默认否)")
    private Boolean append = false;
}


