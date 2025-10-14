package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxly.dto.request.LoginRequest;
import com.yxly.dto.request.RegisterRequest;
import com.yxly.dto.request.ResetPasswordRequest;
import com.yxly.dto.response.LoginResponse;
import com.yxly.entity.SysUser;
import com.yxly.exception.BusinessException;
import com.yxly.mapper.SysUserMapper;
import com.yxly.security.JwtTokenProvider;
import com.yxly.service.AuthService;
import com.yxly.common.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse register(RegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());
        
        // 1. 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "两次输入的密码不一致");
        }
        
        // 2. 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        
        // 3. 检查邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_ALREADY_EXISTS, "邮箱已被注册");
        }
        
        // 4. 检查手机号是否已存在（手机号现在是必填的）
        if (existsByPhone(request.getPhone())) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_EXISTS, "手机号已被注册");
        }
        
        // 5. 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1); // 启用状态
        user.setEmailVerified(0);
        user.setPhoneVerified(0);
        
        userMapper.insert(user);
        
        log.info("用户注册成功: {}", user.getUsername());
        
        // 6. 注册成功后自动登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount(request.getUsername());
        loginRequest.setPassword(request.getPassword());
        
        return login(loginRequest);
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getAccount());
        
        try {
            // 1. 获取用户信息
            SysUser user = findByAccount(request.getAccount());
            if (user == null) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
            }
            
            // 2. 检查用户状态
            if (user.getStatus() == 0) {
                throw new BusinessException(ResultCode.USER_DISABLED, "用户已被禁用");
            }
            
            // 3. 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException(ResultCode.LOGIN_FAILED, "用户名或密码错误");
            }
            
            // 4. 生成Token
            String accessToken = jwtTokenProvider.generateToken(user.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
            Long expiresIn = jwtTokenProvider.getExpirationTime();
            
            // 5. 更新最后登录信息
            user.setLastLoginTime(LocalDateTime.now());
            // 这里可以获取真实IP，暂时使用占位符
            user.setLastLoginIp("127.0.0.1");
            userMapper.updateById(user);
            
            // 6. 构建响应
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roleId(user.getRoleId())
                .lastLoginTime(user.getLastLoginTime())
                .build();
            
            LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userInfo(userInfo)
                .build();
            
            log.info("用户登录成功: {}", user.getUsername());
            return response;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("用户登录失败: {}, 原因: {}", request.getAccount(), e.getMessage());
            throw new BusinessException(ResultCode.LOGIN_FAILED, "登录失败");
        }
    }
    
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // 验证刷新令牌
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "刷新令牌无效");
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        SysUser user = findByAccount(username);
        
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在或已被禁用");
        }
        
        // 生成新的访问令牌
        String newAccessToken = jwtTokenProvider.generateToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .avatar(user.getAvatar())
            .roleId(user.getRoleId())
            .lastLoginTime(user.getLastLoginTime())
            .build();
        
        return LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getExpirationTime())
            .userInfo(userInfo)
            .build();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequest request) {
        // 验证密码一致性
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "两次输入的密码不一致");
        }
        
        // TODO: 验证短信验证码
        // 这里应该验证短信验证码的有效性
        
        // 查找用户
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, request.getPhone())
                .eq(SysUser::getDeleted, 0)
        );
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "手机号未注册");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
        
        log.info("用户重置密码成功: {}", user.getUsername());
    }
    
    @Override
    public SysUser findByAccount(String account) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .and(wrapper -> wrapper
                    .eq(SysUser::getUsername, account)
                    .or()
                    .eq(SysUser::getEmail, account)
                    .or()
                    .eq(SysUser::getPhone, account)
                )
                .eq(SysUser::getDeleted, 0)
        );
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userMapper.selectCount(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0)
        ) > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userMapper.selectCount(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, email)
                .eq(SysUser::getDeleted, 0)
        ) > 0;
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        return userMapper.selectCount(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, phone)
                .eq(SysUser::getDeleted, 0)
        ) > 0;
    }
}
