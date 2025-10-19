package com.yxly.controller.user;

import com.yxly.common.Result;
import com.yxly.dto.response.LocationResponse;
import com.yxly.service.LocationInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端位置控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/location")
@RequiredArgsConstructor
@Tag(name = "用户位置", description = "用户端位置查询相关接口")
public class UserLocationController {

    private final LocationInfoService locationInfoService;

    @Operation(summary = "获取民宿位置信息", description = "获取当前启用的民宿位置信息，用于用户导航")
    @GetMapping
    public Result<LocationResponse> getActiveLocation() {
        
        log.info("用户查询民宿位置信息");
        
        LocationResponse location = locationInfoService.getActiveLocation();
        
        if (location == null) {
            return Result.success(null, "暂无位置信息");
        }
        
        return Result.success(location, "查询成功");
    }

    @Operation(summary = "获取所有启用的位置列表", description = "获取所有启用的民宿位置信息列表")
    @GetMapping("/list")
    public Result<java.util.List<LocationResponse>> getActiveLocationList() {
        
        log.info("用户查询所有启用的位置列表");
        
        java.util.List<LocationResponse> locations = locationInfoService.getActiveLocationList();
        
        return Result.success(locations, "查询成功");
    }
}
