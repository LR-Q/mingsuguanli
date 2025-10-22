package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 民宿主管理员注册请求DTO（简化版）
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@Schema(description = "民宿主管理员注册请求")
public class MerchantRegisterRequest {
    
    @Schema(description = "用户名", example = "merchant001")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度在3-50个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @Schema(description = "密码", example = "Test1234")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6-20个字符")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$", 
             message = "密码至少包含一个字母和一个数字")
    private String password;
    
    @Schema(description = "确认密码", example = "Test1234")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    @Schema(description = "真实姓名", example = "张三")
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    @Schema(description = "手机号", example = "13800138001")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "身份证正面URL")
    @NotBlank(message = "请上传身份证正面")
    private String idCardFront;
    
    @Schema(description = "身份证反面URL")
    @NotBlank(message = "请上传身份证反面")
    private String idCardBack;
}
