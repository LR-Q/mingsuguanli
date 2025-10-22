USE yxly_dev;

-- 查看superadmin当前信息
SELECT 
    id,
    username,
    password,
    role_id,
    status
FROM sys_user 
WHERE username = 'superadmin' AND deleted = 0;

-- 重置superadmin密码为 super123
-- 使用新的BCrypt加密值
UPDATE sys_user 
SET 
    password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCwKnDMz0xQnPMd3j.LKS',
    status = 1,
    role_id = 3
WHERE username = 'superadmin' AND deleted = 0;

-- 验证更新结果
SELECT 
    u.id,
    u.username,
    u.role_id,
    r.role_code,
    r.role_name,
    u.status,
    '密码已重置为: super123' as info
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND u.deleted = 0;
