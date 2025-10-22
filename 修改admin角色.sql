USE yxly_dev;

-- 查看admin当前角色
SELECT '=== admin当前信息 ===' as info;
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
WHERE u.username = 'admin' AND u.deleted = 0;

-- 将admin设置为管理员（HOMESTAY_ADMIN, role_id=1）
UPDATE sys_user 
SET role_id = 1  -- HOMESTAY_ADMIN
WHERE username = 'admin' AND deleted = 0;

-- 验证修改结果
SELECT '=== admin更新后信息 ===' as info;
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
WHERE u.username = 'admin' AND u.deleted = 0;

-- 查看所有角色定义
SELECT '=== 角色定义 ===' as info;
SELECT 
    id as role_id,
    role_code,
    role_name,
    CASE 
        WHEN id = 1 THEN '民宿主管理员'
        WHEN id = 2 THEN '普通用户'
        WHEN id = 3 THEN '超级管理员'
    END as '用途说明'
FROM sys_role
ORDER BY id;
