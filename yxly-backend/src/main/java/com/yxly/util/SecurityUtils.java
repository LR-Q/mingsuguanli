package com.yxly.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxly.entity.SysUser;
import com.yxly.mapper.SysUserMapper;
import com.yxly.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
    private final JwtTokenProvider jwtTokenProvider;
    
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
     * 优先从JWT token中获取userId，如果失败则从数据库查询
     */
    public Long getCurrentUserId() {
        log.info("=== 开始获取当前用户ID ===");
        
        try {
            // 尝试从JWT token中直接获取userId
            String token = getJwtFromRequest();
            log.info("从请求中提取的JWT token: {}", token != null ? "存在" : "不存在");
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                log.info("JWT token验证通过");
                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                log.info("从JWT token中提取的userId: {}", userId);
                
                if (userId != null) {
                    log.info("=== 从JWT token成功获取userId: {} ===", userId);
                    return userId;
                } else {
                    log.warn("JWT token中的userId为null，将尝试从数据库查询");
                }
            } else {
                log.warn("JWT token不存在或验证失败");
            }
        } catch (Exception e) {
            log.error("从JWT token获取userId失败，将尝试从数据库查询", e);
        }
        
        // 如果token中没有userId，回退到数据库查询
        String username = getCurrentUsername();
        log.info("从SecurityContext获取的username: {}", username);
        
        if (username == null) {
            log.warn("=== username为null，无法获取userId ===");
            return null;
        }
        
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0)
                .last("LIMIT 1")
        );
        
        if (user != null) {
            log.info("=== 从数据库查询获取userId: {} (username: {}) ===", user.getId(), user.getUsername());
            return user.getId();
        } else {
            log.error("=== 数据库中未找到用户: {} ===", username);
            return null;
        }
    }
    
    /**
     * 从请求中获取JWT token
     */
    private String getJwtFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String bearerToken = request.getHeader("Authorization");
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    return bearerToken.substring(7);
                }
            }
        } catch (Exception e) {
            log.warn("获取JWT token失败: {}", e.getMessage());
        }
        return null;
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
