package com.yxly.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.Result;
import com.yxly.dto.request.ReviewCreateRequest;
import com.yxly.dto.response.RoomReviewResponse;
import com.yxly.service.RoomReviewService;
import com.yxly.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户评论管理", description = "房间评论相关接口")
@RestController
@RequestMapping("/user/reviews")
@RequiredArgsConstructor
@Slf4j
public class UserReviewController {

    private final RoomReviewService roomReviewService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "提交评论")
    @PostMapping
    public Result<Void> submit(@Validated @RequestBody ReviewCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        roomReviewService.submitReview(userId, request);
        return Result.success();
    }

    @Operation(summary = "按房间获取评论列表")
    @GetMapping("/room/{roomId}")
    public Result<IPage<RoomReviewResponse>> list(
            @Parameter(description = "房间ID") @PathVariable Long roomId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size
    ) {
        return Result.success(roomReviewService.getRoomReviews(roomId, current, size));
    }
}


