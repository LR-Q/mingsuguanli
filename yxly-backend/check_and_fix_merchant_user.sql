-- =============================================
-- 检查和修复用户的 merchant_id 关联
-- =============================================

USE yxly_dev;

-- 1. 查看所有用户的商户关联情况
SELECT 
    u.id AS user_id,
    u.username,
    u.real_name,
    u.role_id,
    r.role_code,
    u.merchant_id,
    mi.merchant_name,
    mi.audit_status
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
LEFT JOIN merchant_info mi ON u.merchant_id = mi.id
WHERE u.deleted = 0
ORDER BY u.id;

-- 2. 查看所有商户信息
SELECT 
    id AS merchant_id,
    admin_user_id,
    merchant_name,
    contact_person,
    contact_phone,
    audit_status,
    CASE audit_status
        WHEN 0 THEN '待审核'
        WHEN 1 THEN '已通过'
        WHEN 2 THEN '已拒绝'
    END AS audit_status_text
FROM merchant_info
WHERE deleted = 0;

-- 3. 自动关联：将 merchant_info 中的 admin_user_id 对应的用户设置 merchant_id
UPDATE sys_user u
INNER JOIN merchant_info mi ON u.id = mi.admin_user_id
SET u.merchant_id = mi.id
WHERE u.deleted = 0 AND mi.deleted = 0 AND u.merchant_id IS NULL;

-- 4. 查看位置信息和商户关联
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

-- 5. 再次查看用户的商户关联情况（验证修复结果）
SELECT 
    u.id AS user_id,
    u.username,
    u.real_name,
    u.role_id,
    r.role_code,
    u.merchant_id,
    mi.merchant_name,
    CASE 
        WHEN u.merchant_id IS NULL THEN '❌ 未关联商户'
        ELSE '✓ 已关联商户'
    END AS status
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
LEFT JOIN merchant_info mi ON u.merchant_id = mi.id
WHERE u.deleted = 0
ORDER BY u.id;

SELECT '✓ 用户商户关联检查和修复完成' AS result;
