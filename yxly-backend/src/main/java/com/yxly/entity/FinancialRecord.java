package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 财务记录实体
 */
@Data
@TableName("financial_record")
public class FinancialRecord {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 记录编号
     */
    @TableField("record_no")
    private String recordNo;
    
    /**
     * 类型(1:收入 2:支出)
     */
    @TableField("type")
    private Integer type;
    
    /**
     * 分类(ROOM/DEPOSIT/REFUND/EXPENSE)
     */
    @TableField("category")
    private String category;
    
    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;
    
    /**
     * 关联订单ID
     */
    @TableField("related_order_id")
    private Long relatedOrderId;
    
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 支付方式
     */
    @TableField("payment_method")
    private String paymentMethod;
    
    /**
     * 操作人ID
     */
    @TableField("operator_id")
    private Long operatorId;
    
    /**
     * 记录日期
     */
    @TableField("record_date")
    private LocalDate recordDate;
    
    /**
     * 凭证图片URL
     */
    @TableField("voucher_url")
    private String voucherUrl;
    
    /**
     * 状态(0:无效 1:有效)
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
