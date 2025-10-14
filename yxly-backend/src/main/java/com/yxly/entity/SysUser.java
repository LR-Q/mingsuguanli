package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yxly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "系统用户")
public class SysUser extends BaseEntity {
    
    @Schema(description = "用户ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "用户名")
    @TableField("username")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度在3-50个字符")
    private String username;
    
    @Schema(description = "密码（BCrypt加密）")
    @TableField("password")
    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过100个字符")
    private String password;
    
    @Schema(description = "真实姓名")
    @TableField("real_name")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    @Schema(description = "邮箱")
    @TableField("email")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Schema(description = "手机号")
    @TableField("phone")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "头像URL")
    @TableField("avatar")
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
    
    @Schema(description = "角色ID")
    @TableField("role_id")
    private Long roleId;
    
    @Schema(description = "状态(0:禁用 1:启用)")
    @TableField("status")
    private Integer status;
    
    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Schema(description = "最后登录IP")
    @TableField("last_login_ip")
    private String lastLoginIp;
    
    @Schema(description = "邮箱验证状态")
    @TableField("email_verified")
    private Integer emailVerified;
    
    @Schema(description = "手机验证状态")
    @TableField("phone_verified")
    private Integer phoneVerified;
}
