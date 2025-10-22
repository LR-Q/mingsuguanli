USE yxly_dev;

-- 创建新的超级管理员账号
-- 用户名：superadmin
-- 密码：super123
-- 角色：SUPER_ADMIN

-- 插入superadmin用户
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
)
SELECT 
    'superadmin',
    '$2a$10$5Z8FvBLOF4KqVYj8nHYOLu0R3jVx7xqK0h.JF2nYpQGRiN9QE.W5K',
    '超级管理员',
    'superadmin@yxly.com',
    '13900000000',
    id,
    1,
    1,
    1,
    0.00,
    NOW(),
    NOW(),
    0
FROM sys_role 
WHERE role_code = 'SUPER_ADMIN' 
LIMIT 1;

-- 验证superadmin创建成功
SELECT 
    u.id,
    u.username,
    u.real_name,
    r.role_name,
    r.role_code,
    u.status
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND u.deleted = 0;

-- 显示admin账号信息（保持不变）
SELECT 
    u.id,
    u.username,
    u.real_name,
    r.role_name,
    r.role_code,
    u.status
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'admin' AND u.deleted = 0;
