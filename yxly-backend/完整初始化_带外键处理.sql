USE yxly_dev;

-- ==========================================
-- 完整初始化脚本（处理外键约束）
-- ==========================================

-- 临时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ========== 步骤1：重建角色表 ==========
DELETE FROM sys_role;
ALTER TABLE sys_role AUTO_INCREMENT = 1;

INSERT INTO sys_role (id, role_name, role_code, description, permissions, create_time, update_time, deleted) VALUES
(1, '管理员（民宿主）', 'HOMESTAY_ADMIN', 'Homestay admin', '["room:*", "order:*", "customer:*", "finance:view"]', NOW(), NOW(), 0),
(2, '用户', 'USER', 'Normal user', '["room:view", "order:create", "order:view"]', NOW(), NOW(), 0),
(3, '超级管理员', 'SUPER_ADMIN', 'Platform super admin', '["*:*"]', NOW(), NOW(), 0);

-- ========== 步骤2：修正现有用户的role_id ==========
-- 如果有role_id不在1,2,3范围的，统一设置为2（普通用户）
UPDATE sys_user 
SET role_id = 2 
WHERE role_id NOT IN (1, 2, 3) AND deleted = 0;

-- ========== 步骤3：创建超级管理员账号 ==========
-- 先检查superadmin是否存在，存在则更新，不存在则插入
INSERT INTO sys_user (
    username, password, real_name, email, phone, 
    role_id, status, email_verified, phone_verified, 
    balance, create_time, update_time, deleted
) 
SELECT 
    'superadmin',
    '$2a$10$5Z8FvBLOF4KqVYj8nHYOLu0R3jVx7xqK0h.JF2nYpQGRiN9QE.W5K',
    '超级管理员',
    'super@admin.com',
    '13900000000',
    3,
    1, 1, 1, 0.00,
    NOW(), NOW(), 0
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_user WHERE username = 'superadmin' AND deleted = 0
);

-- 如果superadmin已存在，更新为超级管理员角色
UPDATE sys_user 
SET role_id = 3, 
    real_name = '超级管理员',
    update_time = NOW()
WHERE username = 'superadmin' AND deleted = 0;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ========== 验证结果 ==========
SELECT '
==========================================
初始化完成！
==========================================
' as result;

SELECT '角色表:' as '---';
SELECT id, role_code, role_name FROM sys_role ORDER BY id;

SELECT '超级管理员账号:' as '---';
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.role_id,
    r.role_code,
    r.role_name
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND u.deleted = 0;

SELECT '
==========================================
超级管理员账号信息:
  用户名: superadmin
  密码: super123
  角色: SUPER_ADMIN (role_id=3)

登录测试:
  http://localhost:3000/login
==========================================
' as info;
