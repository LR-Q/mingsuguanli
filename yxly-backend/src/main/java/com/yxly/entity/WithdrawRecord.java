package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yxly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("withdraw_record")
public class WithdrawRecord extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    
    /**
     * 提现金额
     */
    @TableField("amount")
    private BigDecimal amount;
    
    /**
     * 提现方式
     */
    @TableField("withdraw_method")
    private String withdrawMethod;
    
    /**
     * 收款账户信息
     */
    @TableField("account_info")
    private String accountInfo;
    
    /**
     * 申请状态：0-待审核，1-审核通过，2-审核拒绝
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 申请时间
     */
    @TableField("apply_time")
    private LocalDateTime applyTime;
    
    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;
    
    /**
     * 审核人ID
     */
    @TableField("auditor_id")
    private Long auditorId;
    
    /**
     * 审核人用户名
     */
    @TableField("auditor_name")
    private String auditorName;
    
    /**
     * 审核备注
     */
    @TableField("audit_remark")
    private String auditRemark;
    
    /**
     * 用户备注
     */
    @TableField("user_remark")
    private String userRemark;
}
