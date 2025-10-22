USE yxly_dev;

-- 创建新的超级管理员账号
-- 用户名：superadmin
-- 密码：super123（BCrypt加密后）
-- 角色：SUPER_ADMIN

-- 1. 确保SUPER_ADMIN角色存在
SELECT id INTO @super_admin_role_id FROM sys_role WHERE role_code = 'SUPER_ADMIN' LIMIT 1;

-- 2. 创建superadmin用户（不影响原有admin账号）
INSERT INTO sys_user (
    username,
    password,
    real_name,
    email,
    phone,
    avatar,
    role_id,
    status,
    email_verified,
    phone_verified,
    balance,
    create_time,
    update_time,
    deleted
)
VALUES (
    'superadmin',
    -- BCrypt加密的密码：super123
    '$2a$10$5Z8FvBLOF4KqVYj8nHYOLu0R3jVx7xqK0h.JF2nYpQGRiN9QE.W5K',
    '超级管理员',
    'superadmin@yxly.com',
    '13900000000',
    NULL,
    @super_admin_role_id,
    1,
    1,
    1,
    0.00,
    NOW(),
    NOW(),
    0
);

-- 3. 验证结果
SELECT 
    u.id,
    u.username,
    u.real_name,
    r.role_name,
    r.role_code,
    u.status,
    '创建成功' as result
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND u.deleted = 0;

-- 4. 显示当前admin账号信息（不做修改）
SELECT 
    u.id,
    u.username,
    u.real_name,
    r.role_name,
    r.role_code,
    u.status,
    '保持不变' as result
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'admin' AND u.deleted = 0;
