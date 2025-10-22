USE yxly_dev;

-- 查看所有角色
SELECT '=== 角色表 ===' as info;
SELECT id, role_code, role_name FROM sys_role ORDER BY id;

-- 查看测试用户（查看LLL用户）
SELECT '=== 用户LLL信息 ===' as info;
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.role_id,
    r.role_code,
    r.role_name,
    u.status,
    u.merchant_status
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'LLL' AND u.deleted = 0;

-- 查看superadmin用户
SELECT '=== 超级管理员 ===' as info;
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.role_id,
    r.role_code,
    r.role_name,
    u.status
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username = 'superadmin' AND u.deleted = 0;

-- 如果LLL用户status=0（禁用），需要启用
SELECT '=== 启用LLL用户（如果被禁用） ===' as info;
UPDATE sys_user SET status = 1 WHERE username = 'LLL' AND deleted = 0;

-- 再次查看
SELECT 
    u.username,
    u.role_id,
    r.role_code,
    u.status as '用户状态(1=启用)',
    u.merchant_status as '商户状态(0=待审核,1=已认证)'
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.username IN ('LLL', 'superadmin') AND u.deleted = 0;
