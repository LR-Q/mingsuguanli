package com.yxly.controller.superadmin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.Result;
import com.yxly.dto.response.RoomResponse;
import com.yxly.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页推荐管理（超级管理员）
 */
@RestController
@RequestMapping({"/v1/super-admin/home", "/api/v1/super-admin/home", "/v1/super-admin", "/api/v1/super-admin"})
@RequiredArgsConstructor
public class HomeRecommendController {

    private final RoomService roomService;

    /**
     * 设置房间推荐状态
     */
    @PutMapping("/rooms/{roomId}/recommend")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Boolean> setRecommend(@PathVariable Long roomId, @RequestBody java.util.Map<String, Integer> body) {
        try {
            Integer recommended = body.getOrDefault("recommended", 0);
            roomService.setRoomRecommended(roomId, recommended);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("更新推荐状态失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        }
    }

    /**
     * 推荐房源ID列表
     */
    @GetMapping("/recommendations")
    public Result<List<Long>> getRecommendations() {
        try {
            return Result.success(roomService.getRecommendedRoomIds());
        } catch (Exception e) {
            return Result.success(java.util.Collections.emptyList());
        }
    }

    /**
     * 批量保存推荐房源
     */
    @PutMapping("/recommendations")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Boolean> saveRecommendations(@RequestBody java.util.Map<String, Object> body) {
        try {
            Object idsObj = body.get("roomIds");
            java.util.List<Long> ids = new java.util.ArrayList<>();
            if (idsObj instanceof java.util.List) {
                for (Object o : (java.util.List<?>) idsObj) {
                    if (o instanceof Number) ids.add(((Number) o).longValue());
                }
            }
            roomService.setRecommendations(ids);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("保存首页推荐失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        }
    }

    /**
     * 推荐房源分页（仅可用）
     */
    @GetMapping("/recommendations/page")
    public Result<IPage<RoomResponse>> getRecommendationsPage(@RequestParam(defaultValue = "1") Long current,
                                                              @RequestParam(defaultValue = "12") Long size) {
        return Result.success(roomService.getRecommendedRoomsPage(current, size));
    }
}
