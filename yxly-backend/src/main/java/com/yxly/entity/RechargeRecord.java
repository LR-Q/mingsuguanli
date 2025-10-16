package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yxly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recharge_record")
public class RechargeRecord extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 充值方式（支付宝、微信、银行转账等）
     */
    private String paymentMethod;
    
    /**
     * 支付凭证（上传的图片URL）
     */
    private String paymentProof;
    
    /**
     * 申请状态：0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer status;
    
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 审核人ID
     */
    private Long auditorId;
    
    /**
     * 审核人用户名
     */
    private String auditorName;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 用户备注
     */
    private String userRemark;
}
