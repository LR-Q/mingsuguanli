package com.yxly.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxly.entity.SysUser;
import com.yxly.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security用户详情服务实现
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final SysUserMapper userMapper;
    
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", account);
        
        // 直接查询用户，避免循环依赖
        SysUser user = userMapper.selectOne(
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
        
        if (user == null) {
            log.warn("用户不存在: {}", account);
            throw new UsernameNotFoundException("用户不存在: " + account);
        }
        
        if (user.getStatus() == 0) {
            log.warn("用户已被禁用: {}", account);
            throw new UsernameNotFoundException("用户已被禁用: " + account);
        }
        
        // 这里可以根据角色ID查询具体权限，暂时使用默认权限
        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(user.getStatus() == 0)
            .build();
    }
}
