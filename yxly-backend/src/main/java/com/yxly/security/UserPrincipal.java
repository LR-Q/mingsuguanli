package com.yxly.security;

import com.yxly.entity.SysUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 用户主体
 * 封装完整的 SysUser 对象供后续使用
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    
    private final SysUser user;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 这里可以根据角色ID查询具体权限，暂时使用默认权限
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }
}
