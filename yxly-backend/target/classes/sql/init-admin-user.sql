-- 创建管理员用户
-- 密码: admin123 (BCrypt加密后的值)

-- 先插入管理员角色
INSERT INTO sys_role (role_name, role_code, description, status, create_time, update_time, deleted) 
VALUES ('超级管理员', 'ADMIN', '系统超级管理员，拥有所有权限', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- 获取管理员角色ID (假设为1，实际应该查询)
SET @admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'ADMIN' LIMIT 1);

-- 插入管理员用户
-- 密码 admin123 的BCrypt加密值: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIh9TcOKYHKu6P5wHKxNKYHNka
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
    create_time, 
    update_time, 
    deleted
) VALUES (
    'admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIh9TcOKYHKu6P5wHKxNKYHNka', 
    '系统管理员', 
    'admin@yxly.com', 
    '13800138000', 
    @admin_role_id, 
    1, 
    1, 
    1, 
    NOW(), 
    NOW(), 
    0
) ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    real_name = VALUES(real_name),
    email = VALUES(email),
    phone = VALUES(phone),
    role_id = VALUES(role_id),
    update_time = NOW();
