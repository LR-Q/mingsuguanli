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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户信息实体
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_info")
@Schema(description = "客户信息")
public class CustomerInfo extends BaseEntity {
    
    @Schema(description = "客户ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "客户编号")
    @TableField("customer_no")
    @Size(max = 32, message = "客户编号长度不能超过32个字符")
    private String customerNo;
    
    @Schema(description = "姓名")
    @TableField("name")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;
    
    @Schema(description = "手机号")
    @TableField("phone")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "身份证号")
    @TableField("id_card")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号格式不正确")
    private String idCard;
    
    @Schema(description = "邮箱")
    @TableField("email")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Schema(description = "性别(1:男 2:女)")
    @TableField("gender")
    private Integer gender;
    
    @Schema(description = "生日")
    @TableField("birthday")
    private LocalDate birthday;
    
    @Schema(description = "地址")
    @TableField("address")
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;
    
    @Schema(description = "会员等级")
    @TableField("member_level")
    private Integer memberLevel;
    
    @Schema(description = "累计消费")
    @TableField("total_consumption")
    private BigDecimal totalConsumption;
    
    @Schema(description = "入住次数")
    @TableField("visit_count")
    private Integer visitCount;
    
    @Schema(description = "最后入住时间")
    @TableField("last_visit_time")
    private LocalDateTime lastVisitTime;
    
    @Schema(description = "客户偏好")
    @TableField("preferences")
    private String preferences;
    
    @Schema(description = "备注")
    @TableField("remarks")
    private String remarks;
    
    @Schema(description = "状态(0:黑名单 1:正常)")
    @TableField("status")
    private Integer status;
}
