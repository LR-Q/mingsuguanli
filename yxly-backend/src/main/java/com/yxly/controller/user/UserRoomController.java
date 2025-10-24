package com.yxly.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.response.RoomResponse;
import com.yxly.dto.response.RoomTypeResponse;
import com.yxly.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端房间控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "用户房间", description = "用户端房间查询相关接口")
public class UserRoomController {

    private final RoomService roomService;

    @Operation(summary = "获取可用房间列表", description = "获取状态为可用的房间列表，支持分页和筛选")
    @GetMapping
    public Result<PageResult<RoomResponse>> getAvailableRooms(
            @Parameter(description = "当前页", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "民宿位置ID") @RequestParam(required = false) Long locationId,
            @Parameter(description = "房型ID") @RequestParam(required = false) Long roomTypeId,
            @Parameter(description = "最大入住人数") @RequestParam(required = false) Integer maxGuests,
            @Parameter(description = "最低价格") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) Double maxPrice) {
        
        log.info("用户查询可用房间: current={}, size={}, locationId={}, roomTypeId={}, maxGuests={}, minPrice={}, maxPrice={}", 
                current, size, locationId, roomTypeId, maxGuests, minPrice, maxPrice);
        
        // 只查询状态为可用(1)的房间
        IPage<RoomResponse> page = roomService.getAvailableRoomsForUser(
            current, size, locationId, roomTypeId, maxGuests, minPrice, maxPrice);
        
        PageResult<RoomResponse> result = PageResult.of(
                page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        
        return Result.success(result, "查询成功");
    }

    @Operation(summary = "根据ID查询房间详情", description = "查询指定房间的详细信息")
    @GetMapping("/{id}")
    public Result<RoomResponse> getRoomById(
            @Parameter(description = "房间ID", required = true) @PathVariable Long id) {
        
        log.info("用户查询房间详情: id={}", id);
        
        RoomResponse room = roomService.getRoomById(id);
        
        // 只允许查看可用状态的房间
        if (room != null && room.getStatus() == 1) {
            return Result.success(room, "查询成功");
        } else {
            return Result.error("房间不存在或不可用");
        }
    }

    @Operation(summary = "获取房型列表", description = "获取所有可用的房型")
    @GetMapping("/types")
    public Result<List<RoomTypeResponse>> getRoomTypes() {
        log.info("用户查询房型列表");
        
        List<RoomTypeResponse> roomTypes = roomService.getAllRoomTypes();
        return Result.success(roomTypes, "查询成功");
    }

    @Operation(summary = "搜索房间", description = "根据入住日期和人数搜索可用房间")
    @GetMapping("/search")
    public Result<PageResult<RoomResponse>> searchRooms(
            @Parameter(description = "当前页", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "民宿位置ID") @RequestParam(required = false) Long locationId,
            @Parameter(description = "入住日期", example = "2024-10-20") @RequestParam(required = false) String checkIn,
            @Parameter(description = "退房日期", example = "2024-10-22") @RequestParam(required = false) String checkOut,
            @Parameter(description = "入住人数") @RequestParam(required = false) Integer guests,
            @Parameter(description = "房型ID") @RequestParam(required = false) Long roomTypeId) {
        
        log.info("用户搜索房间: locationId={}, checkIn={}, checkOut={}, guests={}, roomTypeId={}", 
                locationId, checkIn, checkOut, guests, roomTypeId);
        
        // 这里可以后续集成订单系统来检查房间在指定日期的可用性
        // 目前先返回基本的可用房间列表
        IPage<RoomResponse> page = roomService.getAvailableRoomsForUser(
            current, size, locationId, roomTypeId, guests, null, null);
        
        PageResult<RoomResponse> result = PageResult.of(
                page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        
        return Result.success(result, "搜索成功");
    }
}
