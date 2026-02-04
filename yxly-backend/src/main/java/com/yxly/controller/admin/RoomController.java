package com.yxly.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.request.RoomCreateRequest;
import com.yxly.dto.request.RoomUpdateRequest;
import com.yxly.dto.response.LocationSimpleVO;
import com.yxly.dto.response.RoomResponse;
import com.yxly.dto.response.RoomTypeResponse;
import com.yxly.entity.MerchantInfo;
import com.yxly.service.LocationInfoService;
import com.yxly.service.MerchantService;
import com.yxly.service.RoomService;
import com.yxly.service.RoomReviewService;
import com.yxly.dto.response.RoomReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 房间管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/admin/rooms")
@RequiredArgsConstructor
@Validated
@Tag(name = "房间管理", description = "房间管理相关接口")
public class RoomController {

    private final RoomService roomService;
    private final MerchantService merchantService;
    private final LocationInfoService locationInfoService;
    private final RoomReviewService roomReviewService;

    @Operation(summary = "分页查询房间列表", description = "分页查询房间列表，支持多条件筛选")
    @GetMapping
    public Result<PageResult<RoomResponse>> getRoomPage(
            @Parameter(description = "当前页", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "民宿位置ID") @RequestParam(required = false) Long locationId,
            @Parameter(description = "房间号") @RequestParam(required = false) String roomNumber,
            @Parameter(description = "房型ID") @RequestParam(required = false) Long roomTypeId,
            @Parameter(description = "房间状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "楼层") @RequestParam(required = false) Integer floorNumber) {
        
        log.info("分页查询房间列表: current={}, size={}, locationId={}, roomNumber={}, roomTypeId={}, status={}, floorNumber={}", 
                current, size, locationId, roomNumber, roomTypeId, status, floorNumber);
        
        // 基于当前登录用户做隔离：民宿管理员仅能查看自己民宿的房间
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        com.yxly.entity.SysUser currentUser = merchantService.getUserByUsername(currentUsername);
        
        IPage<RoomResponse> page;
        try {
            // 以是否存在商户绑定来判断是否为民宿管理员；不存在则视为平台/超级管理员
            com.yxly.entity.MerchantInfo merchant = (currentUser != null) ? merchantService.getMerchantByAdminUserId(currentUser.getId()) : null;
            if (merchant != null) {
                List<LocationSimpleVO> locations = locationInfoService.getLocationsByMerchant(merchant.getId());
                java.util.List<Long> allowedLocationIds = (locations != null)
                        ? locations.stream().map(LocationSimpleVO::getId).collect(java.util.stream.Collectors.toList())
                        : java.util.Collections.emptyList();
                
                if (allowedLocationIds.isEmpty()) {
                    page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
                } else {
                    if (locationId != null && !allowedLocationIds.contains(locationId)) {
                        // 请求位置不在权限范围，返回空
                        page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
                    } else {
                        java.util.List<Long> filterIds = (locationId != null)
                                ? java.util.Arrays.asList(locationId)
                                : allowedLocationIds;
                        page = roomService.getRoomPageByLocations(current, size, filterIds, roomNumber, roomTypeId, status, floorNumber);
                    }
                }
            } else {
                // 超级管理员或未绑定商户的用户：按入参查询全量
                page = roomService.getRoomPage(current, size, locationId, roomNumber, roomTypeId, status, floorNumber);
            }
        } catch (Exception e) {
            log.error("房间列表查询异常，回退为全量查询: {}", e.getMessage(), e);
            page = roomService.getRoomPage(current, size, locationId, roomNumber, roomTypeId, status, floorNumber);
        }
        
        PageResult<RoomResponse> result = PageResult.of(
                page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        
        return Result.success(result, "查询成功");
    }

    @Operation(summary = "按房间查看评论列表", description = "管理员查看指定房间的用户评论（分页）")
    @GetMapping("/{roomId}/reviews")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<RoomReviewResponse>> getRoomReviews(
            @Parameter(description = "房间ID") @PathVariable Long roomId,
            @Parameter(description = "当前页", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size
    ) {
        com.baomidou.mybatisplus.core.metadata.IPage<RoomReviewResponse> page = roomReviewService.getRoomReviews(roomId, current, size);
        return Result.success(page, "查询成功");
    }
    @Operation(summary = "根据ID查询房间详情", description = "根据房间ID查询房间详细信息")
    @GetMapping("/{id}")
    public Result<RoomResponse> getRoomById(
            @Parameter(description = "房间ID", required = true) @PathVariable @NotNull Long id) {
        
        log.info("查询房间详情: id={}", id);
        
        RoomResponse room = roomService.getRoomById(id);
        return Result.success(room, "查询成功");
    }

    @Operation(summary = "创建房间", description = "创建新的房间")
    @PostMapping
    public Result<Long> createRoom(@RequestBody @Valid RoomCreateRequest request) {
        log.info("创建房间: roomNumber={}", request.getRoomNumber());
        
        Long roomId = roomService.createRoom(request);
        return Result.success(roomId, "房间创建成功");
    }

    @Operation(summary = "更新房间", description = "更新房间信息")
    @PutMapping("/{id}")
    public Result<Void> updateRoom(
            @Parameter(description = "房间ID", required = true) @PathVariable @NotNull Long id,
            @RequestBody @Valid RoomUpdateRequest request) {
        
        log.info("更新房间: id={}", id);
        
        request.setId(id);
        roomService.updateRoom(request);
        return Result.success(null, "房间更新成功");
    }

    @Operation(summary = "删除房间", description = "删除指定房间")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(
            @Parameter(description = "房间ID", required = true) @PathVariable @NotNull Long id) {
        
        log.info("删除房间: id={}", id);
        
        roomService.deleteRoom(id);
        return Result.success(null, "房间删除成功");
    }

    @Operation(summary = "获取房型列表", description = "获取所有启用的房型列表")
    @GetMapping("/types")
    public Result<List<RoomTypeResponse>> getRoomTypes() {
        log.info("查询房型列表");
        
        List<RoomTypeResponse> roomTypes = roomService.getEnabledRoomTypes();
        return Result.success(roomTypes, "查询成功");
    }

    @Operation(summary = "更新房间状态", description = "更新房间状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateRoomStatus(
            @Parameter(description = "房间ID", required = true) @PathVariable @NotNull Long id,
            @Parameter(description = "房间状态", required = true) @RequestParam @NotNull Integer status) {
        
        log.info("更新房间状态: id={}, status={}", id, status);
        
        roomService.updateRoomStatus(id, status);
        return Result.success(null, "房间状态更新成功");
    }
    
    @Operation(summary = "获取当前管理员的民宿位置列表", description = "用于房间管理页面的民宿选择下拉框")
    @GetMapping("/locations")
    public Result<List<LocationSimpleVO>> getAdminLocations(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("获取当前管理员的民宿位置列表");
        
        // 从 Spring Security 获取当前用户
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        
        log.info("当前登录用户: {}", currentUsername);
        
        // 通过用户名获取用户ID
        com.yxly.entity.SysUser currentUser = merchantService.getUserByUsername(currentUsername);
        if (currentUser == null) {
            return Result.success(java.util.Collections.emptyList(), "用户不存在");
        }
        
        // 获取当前用户的商户ID
        MerchantInfo merchantInfo = merchantService.getMerchantByAdminUserId(currentUser.getId());
        if (merchantInfo == null) {
            return Result.success(java.util.Collections.emptyList(), "当前用户未关联商户");
        }
        
        List<LocationSimpleVO> locations = locationInfoService.getLocationsByMerchant(merchantInfo.getId());
        return Result.success(locations, "查询成功");
    }
}


