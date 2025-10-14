package com.yxly.controller.auth;

import com.yxly.common.Result;
import com.yxly.dto.request.LoginRequest;
import com.yxly.dto.request.RegisterRequest;
import com.yxly.dto.request.ResetPasswordRequest;
import com.yxly.dto.response.LoginResponse;
import com.yxly.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户注册、登录、密码重置等认证相关接口")
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "用户注册", description = "新用户注册，注册成功后自动登录")
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());
        LoginResponse response = authService.register(request);
        return Result.success(response, "注册成功");
    }
    
    @Operation(summary = "用户登录", description = "支持用户名/邮箱/手机号登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录请求: {}", request.getAccount());
        LoginResponse response = authService.login(request);
        return Result.success(response, "登录成功");
    }
    
    @Operation(summary = "刷新Token", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        log.info("刷新Token请求");
        LoginResponse response = authService.refreshToken(refreshToken);
        return Result.success(response, "Token刷新成功");
    }
    
    @Operation(summary = "重置密码", description = "通过手机号和短信验证码重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("重置密码请求: {}", request.getPhone());
        authService.resetPassword(request);
        return Result.success(null, "密码重置成功");
    }
    
    @Operation(summary = "检查用户名", description = "检查用户名是否已存在")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = authService.existsByUsername(username);
        return Result.success(!exists, exists ? "用户名已存在" : "用户名可用");
    }
    
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已被注册")
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = authService.existsByEmail(email);
        return Result.success(!exists, exists ? "邮箱已被注册" : "邮箱可用");
    }
    
    @Operation(summary = "检查手机号", description = "检查手机号是否已被注册")
    @GetMapping("/check-phone")
    public Result<Boolean> checkPhone(@RequestParam String phone) {
        boolean exists = authService.existsByPhone(phone);
        return Result.success(!exists, exists ? "手机号已被注册" : "手机号可用");
    }
    
    @Operation(summary = "用户登出", description = "用户登出（前端清除Token即可）")
    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("用户登出");
        // JWT是无状态的，登出只需要前端清除Token
        // 如果需要服务端登出，可以维护一个黑名单
        return Result.success(null, "登出成功");
    }
}
