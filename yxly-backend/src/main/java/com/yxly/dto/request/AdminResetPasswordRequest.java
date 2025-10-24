package com.yxly.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 超级管理员重置商户密码请求DTO
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@Schema(description = "超级管理员重置商户密码请求")
public class AdminResetPasswordRequest {
    
    @Schema(description = "商户ID", example = "1", required = true)
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;
    
    @Schema(description = "新密码（默认为：merchant123）", example = "merchant123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6-20个字符")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$", 
             message = "密码至少包含一个字母和一个数字")
    private String newPassword;
}
