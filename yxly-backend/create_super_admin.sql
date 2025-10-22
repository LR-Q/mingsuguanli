USE yxly_dev;

-- 创建超级管理员账号
-- 用户名：admin
-- 密码：admin123（BCrypt加密后）
-- 角色：SUPER_ADMIN

-- 1. 确保SUPER_ADMIN角色存在
SELECT id INTO @super_admin_role_id FROM sys_role WHERE role_code = 'SUPER_ADMIN' LIMIT 1;

-- 2. 检查admin用户是否已存在
SELECT COUNT(*) INTO @admin_exists FROM sys_user WHERE username = 'admin' AND deleted = 0;

-- 3. 如果不存在，创建admin用户
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
SELECT 
    'admin',
    -- BCrypt加密的密码：admin123
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCwKnDMz0xQnPMd3j.LKS',
    '超级管理员',
    'admin@yxly.com',
    '13800000000',
    NULL,
    @super_admin_role_id,
    1,
    1,
    1,
    0.00,
    NOW(),
    NOW(),
    0
WHERE @admin_exists = 0;

-- 4. 如果已存在，更新为超级管理员角色
UPDATE sys_user 
SET 
    role_id = @super_admin_role_id,
    real_name = '超级管理员',
    status = 1,
    update_time = NOW()
WHERE username = 'admin' AND deleted = 0 AND @admin_exists > 0;

-- 5. 验证结果
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
