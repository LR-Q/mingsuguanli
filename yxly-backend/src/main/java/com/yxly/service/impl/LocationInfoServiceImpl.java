package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.common.ResultCode;
import com.yxly.dto.request.LocationUpdateRequest;
import com.yxly.dto.response.LocationResponse;
import com.yxly.entity.LocationInfo;
import com.yxly.exception.BusinessException;
import com.yxly.mapper.LocationInfoMapper;
import com.yxly.service.LocationInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 位置信息服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationInfoServiceImpl implements LocationInfoService {

    private final LocationInfoMapper locationInfoMapper;

    @Override
    public IPage<LocationResponse> getLocationPage(Long current, Long size) {
        log.info("分页查询位置信息: current={}, size={}", current, size);
        
        Page<LocationInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<LocationInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(LocationInfo::getUpdateTime);
        
        IPage<LocationInfo> locationPage = locationInfoMapper.selectPage(page, wrapper);
        
        // 转换为响应DTO
        return locationPage.convert(location -> {
            LocationResponse response = new LocationResponse();
            BeanUtils.copyProperties(location, response);
            return response;
        });
    }

    @Override
    public LocationResponse getLocationById(Long id) {
        log.info("获取位置信息详情: id={}", id);
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        LocationResponse response = new LocationResponse();
        BeanUtils.copyProperties(location, response);
        return response;
    }

    @Override
    @Transactional
    public Long createLocation(LocationUpdateRequest request) {
        log.info("创建位置信息: name={}", request.getName());
        
        LocationInfo location = new LocationInfo();
        BeanUtils.copyProperties(request, location);
        
        // 如果没有设置地图类型，默认使用百度地图
        if (location.getMapType() == null || location.getMapType().isEmpty()) {
            location.setMapType("baidu");
        }
        
        // 如果没有设置启用状态，默认启用
        if (location.getIsActive() == null) {
            location.setIsActive(1);
        }
        
        int result = locationInfoMapper.insert(location);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "位置信息创建失败");
        }
        
        log.info("位置信息创建成功: locationId={}", location.getId());
        return location.getId();
    }

    @Override
    @Transactional
    public void updateLocation(Long id, LocationUpdateRequest request) {
        log.info("更新位置信息: id={}, name={}", id, request.getName());
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        BeanUtils.copyProperties(request, location);
        location.setId(id);
        
        int result = locationInfoMapper.updateById(location);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "位置信息更新失败");
        }
        
        log.info("位置信息更新成功: locationId={}", id);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        log.info("删除位置信息: id={}", id);
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        int result = locationInfoMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "位置信息删除失败");
        }
        
        log.info("位置信息删除成功: locationId={}", id);
    }

    @Override
    public LocationResponse getActiveLocation() {
        log.info("获取启用的位置信息（用户端）");
        
        LocationInfo location = locationInfoMapper.selectActiveLocation();
        if (location == null) {
            log.warn("未找到启用的位置信息");
            return null;
        }
        
        LocationResponse response = new LocationResponse();
        BeanUtils.copyProperties(location, response);
        
        log.info("获取启用位置成功: locationId={}, name={}", location.getId(), location.getName());
        return response;
    }

    @Override
    public java.util.List<LocationResponse> getActiveLocationList() {
        log.info("获取所有启用的位置列表（用户端）");
        
        LambdaQueryWrapper<LocationInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LocationInfo::getIsActive, 1);
        wrapper.orderByDesc(LocationInfo::getUpdateTime);
        
        java.util.List<LocationInfo> locations = locationInfoMapper.selectList(wrapper);
        
        log.info("获取启用位置列表成功: count={}", locations.size());
        
        return locations.stream()
                .map(location -> {
                    LocationResponse response = new LocationResponse();
                    BeanUtils.copyProperties(location, response);
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleLocationStatus(Long id, Integer isActive) {
        log.info("切换位置启用状态: id={}, isActive={}", id, isActive);
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        location.setIsActive(isActive);
        int result = locationInfoMapper.updateById(location);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "位置状态更新失败");
        }
        
        log.info("位置状态更新成功: locationId={}, isActive={}", id, isActive);
    }
}
