package com.yxly.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxly.entity.SysUser;
import com.yxly.entity.SysRole;
import com.yxly.mapper.SysUserMapper;
import com.yxly.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private final SysRoleMapper roleMapper;
    
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
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.getRoleId() != null) {
            SysRole role = roleMapper.selectById(user.getRoleId());
            if (role != null && role.getStatus() != null && role.getStatus() == 1) {
                String code = role.getRoleCode();
                if (code != null && !code.isEmpty()) {
                    String roleName = "ROLE_" + code.toUpperCase(Locale.ROOT);
                    authorities.add(new SimpleGrantedAuthority(roleName));
                }
            }
        }
        
        return new UserPrincipal(user, authorities);
    }
}
