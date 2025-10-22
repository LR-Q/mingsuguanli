USE yxly_dev;

-- 查看superadmin的完整信息
SELECT 
    u.id,
    u.username,
    u.role_id,
    r.role_code,
    r.role_name,
    u.status,
    CASE 
        WHEN u.role_id = 1 THEN '会跳转到 /admin'
        WHEN u.role_id = 2 THEN '会跳转到 /home (当前问题！)'
        WHEN u.role_id = 3 THEN '会跳转到 /super-admin/merchants'
        ELSE '未知'
    END as '跳转说明'
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND u.deleted = 0;

-- 如果role_id不是3，执行以下修复
UPDATE sys_user 
SET role_id = 3
WHERE username = 'superadmin' AND deleted = 0;

-- 再次验证
SELECT 
    u.username,
    u.role_id,
    r.role_code,
    '应该跳转到 /super-admin/merchants' as info
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND deleted = 0;
