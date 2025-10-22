USE yxly_dev;

-- ==========================================
-- 完整初始化脚本
-- 1. 创建角色
-- 2. 创建超级管理员账号
-- ==========================================

-- ========== 第一步：创建角色 ==========
DELETE FROM sys_role;

INSERT INTO sys_role (id, role_name, role_code, description, permissions, create_time, update_time, deleted) VALUES
(1, '管理员（民宿主）', 'HOMESTAY_ADMIN', 'Homestay admin', '["room:*", "order:*", "customer:*", "finance:view"]', NOW(), NOW(), 0),
(2, '用户', 'USER', 'Normal user', '["room:view", "order:create", "order:view"]', NOW(), NOW(), 0),
(3, '超级管理员', 'SUPER_ADMIN', 'Platform super admin', '["*:*"]', NOW(), NOW(), 0);

-- 验证角色
SELECT '角色创建完成：' as status;
SELECT id, role_code, role_name, description FROM sys_role ORDER BY id;

-- ========== 第二步：创建超级管理员账号 ==========
-- 用户名: superadmin
-- 密码: super123
-- role_id: 3

INSERT INTO sys_user (
    username, 
    password, 
    real_name, 
    email, 
    phone, 
    role_id, 
    status, 
    email_verified, 
    phone_verified, 
    balance, 
    create_time, 
    update_time, 
    deleted
) VALUES (
    'superadmin',
    '$2a$10$5Z8FvBLOF4KqVYj8nHYOLu0R3jVx7xqK0h.JF2nYpQGRiN9QE.W5K',
    '超级管理员',
    'super@admin.com',
    '13900000000',
    3,
    1,
    1,
    1,
    0.00,
    NOW(),
    NOW(),
    0
);

-- 验证超级管理员
SELECT '超级管理员创建完成：' as status;
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.role_id,
    r.role_code,
    r.role_name,
    '密码: super123' as password_hint
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND u.deleted = 0;

-- ========== 完成 ==========
SELECT '
==========================================
初始化完成！
==========================================
角色配置:
  role_id=1: 管理员（民宿主）- HOMESTAY_ADMIN
  role_id=2: 用户 - USER
  role_id=3: 超级管理员 - SUPER_ADMIN

超级管理员账号:
  用户名: superadmin
  密码: super123
  角色: SUPER_ADMIN

登录测试:
  访问: http://localhost:3000/login
  输入: superadmin / super123
  自动跳转到商户审核页面
==========================================
' as result;
