package com.yxly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户审核信息VO
 */
@Data
@Schema(description = "商户审核信息")
public class MerchantAuditVO {
    
    @Schema(description = "商户ID")
    private Long id;
    
    @Schema(description = "管理员用户ID")
    private Long adminUserId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "身份证正面")
    private String idCardFront;
    
    @Schema(description = "身份证反面")
    private String idCardBack;
    
    @Schema(description = "审核状态(0:待审核 1:已认证 2:已拒绝)")
    private Integer auditStatus;
    
    @Schema(description = "申请时间")
    private LocalDateTime applyTime;
    
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    
    @Schema(description = "审核人ID")
    private Long auditorId;
    
    @Schema(description = "审核备注")
    private String auditRemarks;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "状态(0:禁用 1:启用)")
    private Integer status;
}
