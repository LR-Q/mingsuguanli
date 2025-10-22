package com.yxly.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.dto.request.LocationUpdateRequest;
import com.yxly.dto.response.LocationResponse;

/**
 * 位置信息服务接口
 */
public interface LocationInfoService {

    /**
     * 分页查询位置信息列表（管理员）
     *
     * @param current 当前页
     * @param size 每页大小
     * @param merchantId 商户ID（超级管理员传null查看所有）
     * @param roleCode 角色代码
     * @return 位置信息分页结果
     */
    IPage<LocationResponse> getLocationPage(Long current, Long size, Long merchantId, String roleCode);

    /**
     * 获取位置信息详情
     *
     * @param id 位置ID
     * @param merchantId 商户ID（用于权限验证）
     * @param roleCode 角色代码
     * @return 位置信息
     */
    LocationResponse getLocationById(Long id, Long merchantId, String roleCode);

    /**
     * 创建位置信息
     *
     * @param request 位置信息请求
     * @param merchantId 商户ID
     * @return 位置ID
     */
    Long createLocation(LocationUpdateRequest request, Long merchantId);

    /**
     * 更新位置信息
     *
     * @param id 位置ID
     * @param request 位置信息请求
     * @param merchantId 商户ID（用于权限验证）
     * @param roleCode 角色代码
     */
    void updateLocation(Long id, LocationUpdateRequest request, Long merchantId, String roleCode);

    /**
     * 删除位置信息
     *
     * @param id 位置ID
     * @param merchantId 商户ID（用于权限验证）
     * @param roleCode 角色代码
     */
    void deleteLocation(Long id, Long merchantId, String roleCode);

    /**
     * 获取启用的位置信息（用户端）
     *
     * @return 启用的位置信息
     */
    LocationResponse getActiveLocation();

    /**
     * 获取所有启用的位置列表（用户端）
     *
     * @return 所有启用的位置列表
     */
    java.util.List<LocationResponse> getActiveLocationList();

    /**
     * 启用/禁用位置
     *
     * @param id 位置ID
     * @param isActive 是否启用
     * @param merchantId 商户ID（用于权限验证）
     * @param roleCode 角色代码
     */
    void toggleLocationStatus(Long id, Integer isActive, Long merchantId, String roleCode);
}
