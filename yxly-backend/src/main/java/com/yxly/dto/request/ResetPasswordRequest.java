package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 重置密码请求DTO
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordRequest {
    
    @Schema(description = "用户名", example = "jack")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_.-]{3,20}$", message = "用户名为3-20位字母、数字、._-")
    private String username;
    
    @Schema(description = "手机号", example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "邮箱", example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "新密码", example = "123456")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6-20个字符")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$", 
             message = "密码至少包含一个字母和一个数字")
    private String newPassword;
    
    @Schema(description = "确认新密码", example = "123456")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
