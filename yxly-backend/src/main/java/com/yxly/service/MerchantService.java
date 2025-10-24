package com.yxly.service;

import com.yxly.dto.request.AdminResetPasswordRequest;
import com.yxly.dto.request.MerchantAuditRequest;
import com.yxly.dto.request.MerchantRegisterRequest;
import com.yxly.dto.response.LoginResponse;
import com.yxly.dto.response.MerchantAuditVO;
import com.yxly.dto.response.MerchantSimpleVO;

import java.util.List;

/**
 * 商户服务接口
 * 
 * @author yxly
 * @since 2024-01-01
 */
public interface MerchantService {
    
    /**
     * 民宿主管理员注册
     * 
     * @param request 注册请求
     * @return 登录响应（注册成功后自动登录）
     */
    LoginResponse registerMerchant(MerchantRegisterRequest request);
    
    /**
     * 检查民宿名称是否已存在
     * 
     * @param merchantName 民宿名称
     * @return 是否存在
     */
    boolean existsByMerchantName(String merchantName);
    
    /**
     * 获取待审核商户列表
     * 
     * @return 待审核商户列表
     */
    List<MerchantAuditVO> getPendingMerchants();
    
    /**
     * 获取所有商户列表
     * 
     * @param auditStatus 审核状态(null表示查询全部)
     * @return 商户列表
     */
    List<MerchantAuditVO> getAllMerchants(Integer auditStatus);
    
    /**
     * 审核商户
     * 
     * @param request 审核请求
     */
    void auditMerchant(MerchantAuditRequest request);
    
    /**
     * 获取商户详情
     * 
     * @param merchantId 商户ID
     * @return 商户详情
     */
    MerchantAuditVO getMerchantDetail(Long merchantId);
    
    /**
     * 超级管理员重置商户密码
     * 
     * @param request 重置密码请求
     */
    void resetMerchantPassword(AdminResetPasswordRequest request);
    
    /**
     * 获取当前管理员的民宿列表
     * 用于房间管理页面的民宿选择
     * 
     * @param adminUserId 管理员用户ID
     * @return 民宿列表
     */
    List<MerchantSimpleVO> getAdminMerchants(Long adminUserId);
    
    /**
     * 根据管理员用户ID获取商户信息
     * 
     * @param adminUserId 管理员用户ID
     * @return 商户信息
     */
    com.yxly.entity.MerchantInfo getMerchantByAdminUserId(Long adminUserId);
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    com.yxly.entity.SysUser getUserByUsername(String username);
}
