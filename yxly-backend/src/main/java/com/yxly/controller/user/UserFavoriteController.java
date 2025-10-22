package com.yxly.controller.user;

import com.yxly.common.Result;
import com.yxly.dto.request.BatchCheckFavoriteRequest;
import com.yxly.dto.request.FavoriteRequest;
import com.yxly.service.UserFavoriteService;
import com.yxly.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏Controller
 * 
 * @author yxly
 * @since 2024-10-20
 */
@Tag(name = "用户收藏管理", description = "用户收藏相关接口")
@RestController
@RequestMapping("/user/favorites")
@RequiredArgsConstructor
@Slf4j
public class UserFavoriteController {
    
    private final UserFavoriteService userFavoriteService;
    private final SecurityUtils securityUtils;
    
    @Operation(summary = "获取收藏列表")
    @GetMapping
    public Result<List<Map<String, Object>>> getFavoriteList() {
        Long userId = securityUtils.getCurrentUserId();
        List<Map<String, Object>> favorites = userFavoriteService.getFavoriteList(userId);
        return Result.success(favorites);
    }
    
    @Operation(summary = "添加收藏")
    @PostMapping
    public Result<Void> addFavorite(@Validated @RequestBody FavoriteRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        boolean success = userFavoriteService.addFavorite(userId, request.getRoomId());
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("添加收藏失败");
        }
    }
    
    @Operation(summary = "取消收藏")
    @DeleteMapping("/{roomId}")
    public Result<Void> removeFavorite(@Parameter(description = "房间ID") @PathVariable Long roomId) {
        Long userId = securityUtils.getCurrentUserId();
        boolean success = userFavoriteService.removeFavorite(userId, roomId);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("取消收藏失败");
        }
    }
    
    @Operation(summary = "检查房间是否已收藏")
    @GetMapping("/check/{roomId}")
    public Result<Boolean> checkFavorite(@Parameter(description = "房间ID") @PathVariable Long roomId) {
        Long userId = securityUtils.getCurrentUserId();
        boolean isFavorite = userFavoriteService.checkFavorite(userId, roomId);
        return Result.success(isFavorite);
    }
    
    @Operation(summary = "批量检查房间收藏状态")
    @PostMapping("/check-batch")
    public Result<Map<Long, Boolean>> checkBatchFavorites(
            @Validated @RequestBody BatchCheckFavoriteRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        Map<Long, Boolean> result = userFavoriteService.checkBatchFavorites(userId, request.getRoomIds());
        return Result.success(result);
    }
}
