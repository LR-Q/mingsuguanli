package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "房间评论响应")
public class RoomReviewResponse {
    private Long id;
    private Long roomId;
    private Integer rating;
    private String content;
    private String userDisplayName;
    private String userAvatar;
    private Boolean anonymous;
    private LocalDateTime createTime;
}


