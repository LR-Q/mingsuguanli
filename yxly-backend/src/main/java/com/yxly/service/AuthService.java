package com.yxly.service;

import com.yxly.dto.request.LoginRequest;
import com.yxly.dto.request.RegisterRequest;
import com.yxly.dto.request.ResetPasswordRequest;
import com.yxly.dto.request.ChangePasswordRequest;
import com.yxly.dto.response.LoginResponse;
import com.yxly.entity.SysUser;

/**
 * 认证服务接口
 * 
 * @author yxly
 * @since 2024-01-01
 */
public interface AuthService {
    
    /**
     * 用户注册
     * 
     * @param request 注册请求
     * @return 登录响应（注册成功后自动登录）
     */
    LoginResponse register(RegisterRequest request);
    
    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 刷新Token
     * 
     * @param refreshToken 刷新令牌
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 重置密码
     * 
     * @param request 重置密码请求
     */
    void resetPassword(ResetPasswordRequest request);
    
    /**
     * 根据账号查找用户（支持用户名/邮箱/手机号）
     * 
     * @param account 账号
     * @return 用户实体
     */
    SysUser findByAccount(String account);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 修改密码
     * 
     * @param token JWT令牌
     * @param request 修改密码请求
     */
    void changePassword(String token, ChangePasswordRequest request);
}
