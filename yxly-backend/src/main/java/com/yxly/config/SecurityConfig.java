package com.yxly.config;

import com.yxly.security.JwtAuthenticationFilter;
import com.yxly.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    
    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * JWT认证过滤器
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
    
    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // 公开接口
                .antMatchers("/v1/auth/**").permitAll()
                .antMatchers("/v1/merchant/register").permitAll()  // 允许商户注册接口访问
                .antMatchers("/v1/dev/**").permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .antMatchers("/druid/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers(HttpMethod.GET, "/v1/rooms/**").permitAll()
                .antMatchers(HttpMethod.GET, "/v1/admin/rooms/types").permitAll()  // 临时允许房型接口访问
                .antMatchers("/v1/admin/files/**").permitAll()  // 临时允许文件上传接口访问
                .antMatchers("/v1/upload/**").permitAll()  // 允许通用上传接口访问（身份证等）
                .antMatchers("/v1/super-admin/**").permitAll()  // 临时允许超级管理员接口访问（商户审核等）
                .antMatchers("/v1/admin/recharge/**").permitAll()  // 临时允许管理员充值管理接口访问
                .antMatchers("/v1/admin/withdraw/**").permitAll()  // 临时允许管理员提现管理接口访问
                .antMatchers("/v1/admin/bookings/**").permitAll()  // 临时允许管理员订单管理接口访问
                .antMatchers("/admin/bookings/**").permitAll()  // 临时允许管理员订单管理接口访问（新路径）
                .antMatchers("/v1/user/recharge/**").permitAll()  // 临时允许用户充值接口访问（调试用）
                .antMatchers("/v1/user/info").permitAll()  // 临时允许用户信息接口访问（调试用）
                .antMatchers("/v1/user/balance").permitAll()  // 临时允许用户余额接口访问（调试用）
                .antMatchers("/user/bookings/**").permitAll()  // 临时允许用户订单接口访问（调试用）
                .antMatchers("/v1/user/financial-records/**").permitAll()  // 临时允许用户财务记录接口访问（调试用）
                // 需要认证的接口
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
