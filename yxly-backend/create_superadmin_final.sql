USE yxly_dev;

-- 创建超级管理员账号
-- 用户名: superadmin
-- 密码: super123
-- role_id: 3 (超级管理员)

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
    3,  -- 超级管理员
    1,
    1,
    1,
    0.00,
    NOW(),
    NOW(),
    0
);

-- 验证创建结果
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
