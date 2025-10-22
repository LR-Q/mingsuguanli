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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.yxly.security.UserPrincipal;
import com.yxly.entity.SysUser;

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
        
        // 获取当前登录用户信息
        SysUser currentUser = getCurrentUser();
        Long merchantId = currentUser.getMerchantId();
        String roleCode = getRoleCode(currentUser);
        
        log.info("=== Controller - 分页查询位置列表 ===");
        log.info("用户ID: {}, 用户名: {}", currentUser.getId(), currentUser.getUsername());
        log.info("merchantId: {}, roleId: {}, roleCode: {}", merchantId, currentUser.getRoleId(), roleCode);
        log.info("分页参数: current={}, size={}", current, size);
        log.info("====================================");
        
        IPage<LocationResponse> page = locationInfoService.getLocationPage(current, size, merchantId, roleCode);
        
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
        
        // 获取当前登录用户信息
        SysUser currentUser = getCurrentUser();
        Long merchantId = currentUser.getMerchantId();
        String roleCode = getRoleCode(currentUser);
        
        log.info("查询位置详情: id={}, merchantId={}, roleCode={}", id, merchantId, roleCode);
        
        LocationResponse location = locationInfoService.getLocationById(id, merchantId, roleCode);
        return Result.success(location, "查询成功");
    }

    @Operation(summary = "创建位置", description = "创建新的位置信息")
    @PostMapping
    public Result<Long> createLocation(@Valid @RequestBody LocationUpdateRequest request) {
        
        // 获取当前登录用户的商户ID
        SysUser currentUser = getCurrentUser();
        Long merchantId = currentUser.getMerchantId();
        
        log.info("=== 创建位置 - 详细信息 ===");
        log.info("用户ID: {}", currentUser.getId());
        log.info("用户名: {}", currentUser.getUsername());
        log.info("真实姓名: {}", currentUser.getRealName());
        log.info("角色ID: {}", currentUser.getRoleId());
        log.info("商户ID: {}", merchantId);
        log.info("位置名称: {}", request.getName());
        log.info("位置地址: {}", request.getAddress());
        log.info("=========================");
        
        Long locationId = locationInfoService.createLocation(request, merchantId);
        return Result.success(locationId, "创建成功");
    }

    @Operation(summary = "更新位置", description = "更新位置信息")
    @PutMapping("/{id}")
    public Result<Void> updateLocation(
            @Parameter(description = "位置ID", required = true) @PathVariable @NotNull Long id,
            @Valid @RequestBody LocationUpdateRequest request) {
        
        // 获取当前登录用户信息
        SysUser currentUser = getCurrentUser();
        Long merchantId = currentUser.getMerchantId();
        String roleCode = getRoleCode(currentUser);
        
        log.info("更新位置 - 用户信息: userId={}, username={}, merchantId={}, roleId={}, roleCode={}", 
                currentUser.getId(), currentUser.getUsername(), merchantId, currentUser.getRoleId(), roleCode);
        log.info("更新位置 - 请求参数: locationId={}, name={}, address={}", id, request.getName(), request.getAddress());
        
        locationInfoService.updateLocation(id, request, merchantId, roleCode);
        return Result.success(null, "更新成功");
    }

    @Operation(summary = "删除位置", description = "删除位置信息（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteLocation(
            @Parameter(description = "位置ID", required = true) @PathVariable @NotNull Long id) {
        
        // 获取当前登录用户信息
        SysUser currentUser = getCurrentUser();
        Long merchantId = currentUser.getMerchantId();
        String roleCode = getRoleCode(currentUser);
        
        log.info("删除位置: id={}, merchantId={}, roleCode={}", id, merchantId, roleCode);
        
        locationInfoService.deleteLocation(id, merchantId, roleCode);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "启用/禁用位置", description = "切换位置的启用状态")
    @PatchMapping("/{id}/status")
    public Result<Void> toggleLocationStatus(
            @Parameter(description = "位置ID", required = true) @PathVariable @NotNull Long id,
            @Parameter(description = "是否启用(0:禁用 1:启用)", required = true) @RequestParam @NotNull Integer isActive) {
        
        // 获取当前登录用户信息
        SysUser currentUser = getCurrentUser();
        Long merchantId = currentUser.getMerchantId();
        String roleCode = getRoleCode(currentUser);
        
        log.info("切换位置状态: id={}, isActive={}, merchantId={}, roleCode={}", id, isActive, merchantId, roleCode);
        
        locationInfoService.toggleLocationStatus(id, isActive, merchantId, roleCode);
        return Result.success(null, "状态更新成功");
    }
    
    /**
     * 获取当前登录用户
     */
    private SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new RuntimeException("未登录或登录已过期");
    }
    
    /**
     * 获取用户角色代码
     */
    private String getRoleCode(SysUser user) {
        // 根据数据库 sys_role 表的实际映射
        if (user.getRoleId() == null) {
            return "USER";
        }
        // 1=HOMESTAY_ADMIN(管理员/民宿主), 2=USER(用户), 3=SUPER_ADMIN(超级管理员)
        if (user.getRoleId() == 3L) {
            return "SUPER_ADMIN";
        } else if (user.getRoleId() == 1L) {
            return "HOMESTAY_ADMIN";
        } else if (user.getRoleId() == 2L) {
            return "USER";
        }
        return "USER";
    }
}
