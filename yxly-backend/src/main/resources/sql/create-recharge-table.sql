-- 创建充值记录表
CREATE TABLE IF NOT EXISTS `recharge_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '充值金额',
    `payment_method` VARCHAR(50) NOT NULL COMMENT '充值方式（支付宝、微信、银行转账等）',
    `payment_proof` VARCHAR(500) NOT NULL COMMENT '支付凭证（上传的图片URL）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '申请状态：0-待审核，1-审核通过，2-审核拒绝',
    `apply_time` DATETIME NOT NULL COMMENT '申请时间',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `auditor_id` BIGINT NULL COMMENT '审核人ID',
    `auditor_name` VARCHAR(50) NULL COMMENT '审核人用户名',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `user_remark` VARCHAR(500) NULL COMMENT '用户备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_apply_time` (`apply_time`),
    INDEX `idx_audit_time` (`audit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- 为用户表添加余额字段（如果不存在）
ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额';

-- 创建索引
CREATE INDEX IF NOT EXISTS `idx_balance` ON `sys_user` (`balance`);
