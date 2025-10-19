package com.yxly.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.request.LocationUpdateRequest;
import com.yxly.dto.response.LocationResponse;
import com.yxly.service.LocationInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 位置管理控制器（管理员）
 */
@Slf4j
@RestController
@RequestMapping("/v1/admin/locations")
@RequiredArgsConstructor
@Validated
@Tag(name = "位置管理", description = "位置管理相关接口（管理员）")
public class LocationController {

    private final LocationInfoService locationInfoService;

    @Operation(summary = "分页查询位置列表", description = "分页查询位置列表")
    @GetMapping
    public Result<PageResult<LocationResponse>> getLocationPage(
            @Parameter(description = "当前页", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size) {
        
        log.info("分页查询位置列表: current={}, size={}", current, size);
        
        IPage<LocationResponse> page = locationInfoService.getLocationPage(current, size);
        
        PageResult<LocationResponse> result = PageResult.of(
                page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        
        return Result.success(result, "查询成功");
    }

    @Operation(summary = "根据ID查询位置详情", description = "根据位置ID查询详细信息")
    @GetMapping("/{id}")
    public Result<LocationResponse> getLocationById(
            @Parameter(description = "位置ID", required = true) @PathVariable @NotNull Long id) {
        
        log.info("查询位置详情: id={}", id);
        
        LocationResponse location = locationInfoService.getLocationById(id);
        return Result.success(location, "查询成功");
    }

    @Operation(summary = "创建位置", description = "创建新的位置信息")
    @PostMapping
    public Result<Long> createLocation(@Valid @RequestBody LocationUpdateRequest request) {
        
        log.info("创建位置: name={}, address={}", request.getName(), request.getAddress());
        
        Long locationId = locationInfoService.createLocation(request);
        return Result.success(locationId, "创建成功");
    }

    @Operation(summary = "更新位置", description = "更新位置信息")
    @PutMapping("/{id}")
    public Result<Void> updateLocation(
            @Parameter(description = "位置ID", required = true) @PathVariable @NotNull Long id,
            @Valid @RequestBody LocationUpdateRequest request) {
        
        log.info("更新位置: id={}, name={}, address={}", id, request.getName(), request.getAddress());
        
        locationInfoService.updateLocation(id, request);
        return Result.success(null, "更新成功");
    }

    @Operation(summary = "删除位置", description = "删除位置信息（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteLocation(
            @Parameter(description = "位置ID", required = true) @PathVariable @NotNull Long id) {
        
        log.info("删除位置: id={}", id);
        
        locationInfoService.deleteLocation(id);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "启用/禁用位置", description = "切换位置的启用状态")
    @PatchMapping("/{id}/status")
    public Result<Void> toggleLocationStatus(
            @Parameter(description = "位置ID", required = true) @PathVariable @NotNull Long id,
            @Parameter(description = "是否启用(0:禁用 1:启用)", required = true) @RequestParam @NotNull Integer isActive) {
        
        log.info("切换位置状态: id={}, isActive={}", id, isActive);
        
        locationInfoService.toggleLocationStatus(id, isActive);
        return Result.success(null, "状态更新成功");
    }
}
