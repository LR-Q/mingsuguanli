package com.yxly.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxly.entity.SysUser;
import com.yxly.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {
    
    private final SysUserMapper userMapper;
    
    /**
     * 获取当前登录用户
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
    
    /**
     * 获取当前登录用户ID
     */
    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .and(wrapper -> wrapper
                    .eq(SysUser::getUsername, username)
                    .or()
                    .eq(SysUser::getEmail, username)
                    .or()
                    .eq(SysUser::getPhone, username)
                )
                .eq(SysUser::getDeleted, 0)
        );
        
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前登录用户信息
     */
    public SysUser getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        
        return userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .and(wrapper -> wrapper
                    .eq(SysUser::getUsername, username)
                    .or()
                    .eq(SysUser::getEmail, username)
                    .or()
                    .eq(SysUser::getPhone, username)
                )
                .eq(SysUser::getDeleted, 0)
        );
    }
}
