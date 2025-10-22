-- 检查 XYY 用户的详细信息
USE yxly_dev;

-- 1. 查询 XYY 用户的完整信息
SELECT 
    u.id AS user_id,
    u.username,
    u.real_name,
    u.role_id,
    r.role_code,
    r.role_name,
    u.merchant_id,
    mi.merchant_name,
    mi.admin_user_id,
    u.status,
    u.deleted
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
LEFT JOIN merchant_info mi ON u.merchant_id = mi.id
WHERE u.username = 'XYY';

-- 2. 查询所有位置的归属情况
SELECT 
    loc.id AS location_id,
    loc.merchant_id,
    loc.name AS location_name,
    mi.merchant_name,
    mi.admin_user_id,
    u.username AS admin_username
FROM location_info loc
LEFT JOIN merchant_info mi ON loc.merchant_id = mi.id
LEFT JOIN sys_user u ON mi.admin_user_id = u.id
WHERE loc.deleted = 0
ORDER BY loc.id;

-- 3. 预期结果：
-- XYY 的 merchant_id 应该是 2
-- XYY 的 role_id 应该是 2 (HOMESTAY_ADMIN) 或 1 (SUPER_ADMIN)
-- 如果 role_id=2 且 merchant_id=2，XYY 只应该看到 merchant_id=2 的位置
-- 如果 role_id=1，XYY 可以看到所有位置

-- 4. 如果 XYY 的角色是超级管理员但不应该是，执行：
-- UPDATE sys_user SET role_id = 2 WHERE username = 'XYY';

-- 5. 如果 XYY 的 merchant_id 不是 2，执行：
-- UPDATE sys_user SET merchant_id = 2 WHERE username = 'XYY';
