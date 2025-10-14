package com.yxly.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户信息响应DTO
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@Schema(description = "客户信息响应")
public class CustomerResponse {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "状态", example = "1")
    private Integer status;
    
    @Schema(description = "状态描述")
    private String statusText;
    
    @Schema(description = "邮箱验证状态")
    private Integer emailVerified;
    
    @Schema(description = "手机验证状态")
    private Integer phoneVerified;
    
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    
    @Schema(description = "最后登录IP")
    private String lastLoginIp;
    
    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
