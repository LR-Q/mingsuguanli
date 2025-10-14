package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yxly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 系统角色实体
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "系统角色")
public class SysRole extends BaseEntity {
    
    @Schema(description = "角色ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "角色名称")
    @TableField("role_name")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;
    
    @Schema(description = "角色编码")
    @TableField("role_code")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 20, message = "角色编码长度不能超过20个字符")
    private String roleCode;
    
    @Schema(description = "角色描述")
    @TableField("description")
    private String description;
    
    @Schema(description = "权限列表")
    @TableField("permissions")
    private String permissions;
    
    @Schema(description = "状态(0:禁用 1:启用)")
    @TableField("status")
    private Integer status;
}
