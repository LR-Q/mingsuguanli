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
    public IPage<LocationResponse> getLocationPage(Long current, Long size, Long merchantId, String roleCode) {
        log.info("=== 分页查询位置信息（所有管理员共享查看）===");
        log.info("current: {}, size: {}", current, size);
        log.info("merchantId: {}", merchantId);
        log.info("roleCode: {}", roleCode);
        
        Page<LocationInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<LocationInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 所有管理员都可以查看所有位置（共享）
        // 不需要按 merchantId 过滤
        log.info("✅ 查询所有位置（管理员共享查看）");
        
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
    public LocationResponse getLocationById(Long id, Long merchantId, String roleCode) {
        log.info("获取位置信息详情: id={}, merchantId={}, roleCode={}", id, merchantId, roleCode);
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        // 所有管理员都可以查看位置详情（共享）
        // 不需要验证 merchantId
        log.info("✅ 查看位置详情（管理员共享查看）: locationId={}, locationMerchantId={}", 
                id, location.getMerchantId());
        
        LocationResponse response = new LocationResponse();
        BeanUtils.copyProperties(location, response);
        return response;
    }

    @Override
    @Transactional
    public Long createLocation(LocationUpdateRequest request, Long merchantId) {
        log.info("=== Service层 - 创建位置信息 ===");
        log.info("接收到的 merchantId: {}", merchantId);
        log.info("位置名称: {}", request.getName());
        
        if (merchantId == null) {
            log.error("商户ID为空，无法创建位置");
            throw new BusinessException(ResultCode.PARAM_ERROR, "商户ID不能为空");
        }
        
        LocationInfo location = new LocationInfo();
        BeanUtils.copyProperties(request, location);
        
        // 设置商户ID
        location.setMerchantId(merchantId);
        log.info("设置位置的 merchantId: {}", location.getMerchantId());
        
        // 如果没有设置地图类型，默认使用百度地图
        if (location.getMapType() == null || location.getMapType().isEmpty()) {
            location.setMapType("baidu");
        }
        
        // 如果没有设置启用状态，默认启用
        if (location.getIsActive() == null) {
            location.setIsActive(1);
        }
        
        log.info("准备插入数据库，location对象: id={}, merchantId={}, name={}", 
                location.getId(), location.getMerchantId(), location.getName());
        
        int result = locationInfoMapper.insert(location);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "位置信息创建失败");
        }
        
        log.info("位置信息创建成功: locationId={}, merchantId={}", location.getId(), location.getMerchantId());
        
        // 再次从数据库查询确认
        LocationInfo savedLocation = locationInfoMapper.selectById(location.getId());
        log.info("数据库中保存的 merchantId: {}", savedLocation.getMerchantId());
        
        return location.getId();
    }

    @Override
    @Transactional
    public void updateLocation(Long id, LocationUpdateRequest request, Long merchantId, String roleCode) {
        log.info("更新位置信息: id={}, name={}, merchantId={}, roleCode={}", id, request.getName(), merchantId, roleCode);
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        // 如果不是超级管理员，验证位置归属
        if (!"SUPER_ADMIN".equals(roleCode)) {
            // 检查用户是否有 merchantId
            if (merchantId == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "当前用户没有关联商户，无权操作位置信息");
            }
            // 检查位置是否有 merchantId
            if (location.getMerchantId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "此位置未关联商户，无权操作");
            }
            // 检查是否是同一个商户
            if (!location.getMerchantId().equals(merchantId)) {
                throw new BusinessException(ResultCode.FORBIDDEN, "无权修改此位置信息");
            }
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
    public void deleteLocation(Long id, Long merchantId, String roleCode) {
        log.info("删除位置信息: id={}, merchantId={}, roleCode={}", id, merchantId, roleCode);
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        // 如果不是超级管理员，验证位置归属
        if (!"SUPER_ADMIN".equals(roleCode)) {
            // 检查用户是否有 merchantId
            if (merchantId == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "当前用户没有关联商户，无权删除位置信息");
            }
            // 检查位置是否有 merchantId
            if (location.getMerchantId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "此位置未关联商户，无权删除");
            }
            // 检查是否是同一个商户
            if (!location.getMerchantId().equals(merchantId)) {
                throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此位置信息");
            }
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
    public void toggleLocationStatus(Long id, Integer isActive, Long merchantId, String roleCode) {
        log.info("切换位置启用状态: id={}, isActive={}, merchantId={}, roleCode={}", id, isActive, merchantId, roleCode);
        
        LocationInfo location = locationInfoMapper.selectByIdWithoutDeleted(id);
        if (location == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "位置信息不存在");
        }
        
        // 如果不是超级管理员，验证位置归属
        if (!"SUPER_ADMIN".equals(roleCode)) {
            // 检查用户是否有 merchantId
            if (merchantId == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "当前用户没有关联商户，无权修改位置状态");
            }
            // 检查位置是否有 merchantId
            if (location.getMerchantId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "此位置未关联商户，无权修改状态");
            }
            // 检查是否是同一个商户
            if (!location.getMerchantId().equals(merchantId)) {
                throw new BusinessException(ResultCode.FORBIDDEN, "无权修改此位置状态");
            }
        }
        
        location.setIsActive(isActive);
        int result = locationInfoMapper.updateById(location);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "位置状态更新失败");
        }
        
        log.info("位置状态更新成功: locationId={}, isActive={}", id, isActive);
    }
}
