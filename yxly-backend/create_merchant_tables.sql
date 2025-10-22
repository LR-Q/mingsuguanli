-- =============================================
-- 民宿主管理员注册功能 - 数据库表结构
-- 使用说明：直接在数据库中执行此脚本
-- =============================================

USE yxly_dev;

-- 1. 检查并扩展sys_user表
ALTER TABLE sys_user
ADD COLUMN IF NOT EXISTS merchant_id BIGINT DEFAULT NULL COMMENT '商户ID（民宿主专用）',
ADD COLUMN IF NOT EXISTS merchant_status INT DEFAULT 0 COMMENT '商户状态（0待审核 1已认证 2已拒绝）',
ADD COLUMN IF NOT EXISTS apply_time DATETIME DEFAULT NULL COMMENT '申请时间';

-- 添加索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_merchant_id ON sys_user(merchant_id);
CREATE INDEX IF NOT EXISTS idx_merchant_status ON sys_user(merchant_status);

-- 2. 创建商户信息表
DROP TABLE IF EXISTS merchant_info;
CREATE TABLE merchant_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商户ID',
    admin_user_id BIGINT NOT NULL COMMENT '管理员用户ID',
    merchant_name VARCHAR(100) NOT NULL COMMENT '民宿名称',
    merchant_code VARCHAR(50) NOT NULL COMMENT '商户编码',
    business_license VARCHAR(200) DEFAULT NULL COMMENT '营业执照号',
    
    -- 联系信息
    contact_person VARCHAR(50) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    contact_email VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
    
    -- 地址信息
    province VARCHAR(50) DEFAULT NULL COMMENT '省份',
    city VARCHAR(50) DEFAULT NULL COMMENT '城市',
    district VARCHAR(50) DEFAULT NULL COMMENT '区县',
    address VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
    
    -- 资质证明
    id_card_front VARCHAR(200) DEFAULT NULL COMMENT '身份证正面',
    id_card_back VARCHAR(200) DEFAULT NULL COMMENT '身份证反面',
    business_license_img VARCHAR(200) DEFAULT NULL COMMENT '营业执照图片',
    property_proof VARCHAR(500) DEFAULT NULL COMMENT '房产证明',
    
    -- 银行账户
    bank_name VARCHAR(100) DEFAULT NULL COMMENT '开户银行',
    bank_account VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    account_name VARCHAR(50) DEFAULT NULL COMMENT '账户名',
    
    -- 审核信息
    audit_status INT DEFAULT 0 COMMENT '审核状态（0待审核 1已通过 2已拒绝）',
    audit_time DATETIME DEFAULT NULL COMMENT '审核时间',
    auditor_id BIGINT DEFAULT NULL COMMENT '审核人ID',
    audit_remarks TEXT DEFAULT NULL COMMENT '审核备注',
    
    -- 业务信息
    settlement_rate DECIMAL(5,2) DEFAULT 100.00 COMMENT '结算比例',
    status INT DEFAULT 1 COMMENT '状态（0禁用 1启用）',
    
    -- 统计信息
    room_count INT DEFAULT 0 COMMENT '房源数量',
    order_count INT DEFAULT 0 COMMENT '订单数量',
    total_revenue DECIMAL(15,2) DEFAULT 0.00 COMMENT '总营收',
    
    -- 系统字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    
    UNIQUE KEY uk_merchant_code (merchant_code),
    UNIQUE KEY uk_admin_user_id (admin_user_id),
    INDEX idx_audit_status (audit_status),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户信息表';

-- 3. 插入角色数据
INSERT INTO sys_role (role_name, role_code, description, permissions, status, create_time, update_time, deleted) 
VALUES 
('超级管理员', 'SUPER_ADMIN', '平台超级管理员，拥有所有权限', '["*"]', 1, NOW(), NOW(), 0),
('民宿主管理员', 'HOMESTAY_ADMIN', '民宿主管理员，管理自己的民宿', '["room:*","order:*","customer:*","finance:view"]', 1, NOW(), NOW(), 0),
('普通用户', 'USER', '普通用户，可预订房间', '["room:view","order:create","order:view"]', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE 
    role_name = VALUES(role_name),
    description = VALUES(description),
    permissions = VALUES(permissions),
    update_time = NOW();

-- 4. 更新admin用户为超级管理员
UPDATE sys_user 
SET role_id = (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN' LIMIT 1)
WHERE username = 'admin' AND deleted = 0;

-- 5. 为普通用户设置USER角色
UPDATE sys_user 
SET role_id = (SELECT id FROM sys_role WHERE role_code = 'USER' LIMIT 1)
WHERE role_id IS NULL AND username != 'admin' AND deleted = 0;

-- 6. 扩展room_info表
ALTER TABLE room_info
ADD COLUMN IF NOT EXISTS merchant_id BIGINT DEFAULT NULL COMMENT '商户ID';

CREATE INDEX IF NOT EXISTS idx_room_merchant_id ON room_info(merchant_id);

-- 7. 扩展booking_order表
ALTER TABLE booking_order
ADD COLUMN IF NOT EXISTS merchant_id BIGINT DEFAULT NULL COMMENT '商户ID';

CREATE INDEX IF NOT EXISTS idx_order_merchant_id ON booking_order(merchant_id);

-- 8. 创建商户申请记录表（可选，用于记录申请历史）
DROP TABLE IF EXISTS merchant_apply_log;
CREATE TABLE merchant_apply_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    merchant_name VARCHAR(100) NOT NULL COMMENT '民宿名称',
    apply_data JSON DEFAULT NULL COMMENT '申请数据',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    audit_status INT DEFAULT 0 COMMENT '审核状态',
    audit_time DATETIME DEFAULT NULL COMMENT '审核时间',
    auditor_id BIGINT DEFAULT NULL COMMENT '审核人',
    audit_remarks TEXT DEFAULT NULL COMMENT '审核备注',
    
    INDEX idx_user_id (user_id),
    INDEX idx_apply_time (apply_time),
    INDEX idx_audit_status (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户申请记录表';

-- 查看结果
SELECT '商户信息表创建成功' AS message;
SELECT * FROM sys_role WHERE role_code IN ('SUPER_ADMIN', 'HOMESTAY_ADMIN', 'USER');
