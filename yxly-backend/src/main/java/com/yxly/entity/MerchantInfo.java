package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yxly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户信息实体
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_info")
@Schema(description = "商户信息")
public class MerchantInfo extends BaseEntity {
    
    @Schema(description = "商户ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "管理员用户ID")
    @TableField("admin_user_id")
    private Long adminUserId;
    
    @Schema(description = "民宿名称")
    @TableField("merchant_name")
    @NotBlank(message = "民宿名称不能为空")
    @Size(max = 100, message = "民宿名称长度不能超过100个字符")
    private String merchantName;
    
    @Schema(description = "商户编码")
    @TableField("merchant_code")
    private String merchantCode;
    
    @Schema(description = "营业执照号")
    @TableField("business_license")
    @Size(max = 200, message = "营业执照号长度不能超过200个字符")
    private String businessLicense;
    
    @Schema(description = "联系人")
    @TableField("contact_person")
    @NotBlank(message = "联系人不能为空")
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactPerson;
    
    @Schema(description = "联系电话")
    @TableField("contact_phone")
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;
    
    @Schema(description = "联系邮箱")
    @TableField("contact_email")
    @Size(max = 100, message = "联系邮箱长度不能超过100个字符")
    private String contactEmail;
    
    @Schema(description = "省份")
    @TableField("province")
    private String province;
    
    @Schema(description = "城市")
    @TableField("city")
    private String city;
    
    @Schema(description = "区县")
    @TableField("district")
    private String district;
    
    @Schema(description = "详细地址")
    @TableField("address")
    private String address;
    
    @Schema(description = "身份证正面")
    @TableField("id_card_front")
    private String idCardFront;
    
    @Schema(description = "身份证反面")
    @TableField("id_card_back")
    private String idCardBack;
    
    @Schema(description = "营业执照图片")
    @TableField("business_license_img")
    private String businessLicenseImg;
    
    @Schema(description = "房产证明")
    @TableField("property_proof")
    private String propertyProof;
    
    @Schema(description = "开户银行")
    @TableField("bank_name")
    private String bankName;
    
    @Schema(description = "银行账号")
    @TableField("bank_account")
    private String bankAccount;
    
    @Schema(description = "账户名")
    @TableField("account_name")
    private String accountName;
    
    @Schema(description = "审核状态(0:待审核 1:已通过 2:已拒绝)")
    @TableField("audit_status")
    private Integer auditStatus;
    
    @Schema(description = "审核时间")
    @TableField("audit_time")
    private LocalDateTime auditTime;
    
    @Schema(description = "审核人ID")
    @TableField("auditor_id")
    private Long auditorId;
    
    @Schema(description = "审核备注")
    @TableField("audit_remarks")
    private String auditRemarks;
    
    @Schema(description = "结算比例")
    @TableField("settlement_rate")
    private BigDecimal settlementRate;
    
    @Schema(description = "状态(0:禁用 1:启用)")
    @TableField("status")
    private Integer status;
    
    @Schema(description = "房源数量")
    @TableField("room_count")
    private Integer roomCount;
    
    @Schema(description = "订单数量")
    @TableField("order_count")
    private Integer orderCount;
    
    @Schema(description = "总营收")
    @TableField("total_revenue")
    private BigDecimal totalRevenue;
}
