package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户登录请求DTO
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户登录请求")
public class LoginRequest {
    
    @Schema(description = "登录账号（用户名/邮箱/手机号）", example = "admin")
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 100, message = "登录账号长度不能超过100个字符")
    private String account;
    
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过100个字符")
    private String password;
    
    @Schema(description = "记住登录", example = "true")
    private Boolean rememberMe = false;
    
    @Schema(description = "登录类型", example = "user", allowableValues = {"user", "admin"})
    private String userType = "user";
}
