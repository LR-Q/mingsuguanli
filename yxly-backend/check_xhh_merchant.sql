-- 检查小灰灰用户的商户ID
USE yxly_dev;

-- 1. 查询小灰灰用户的信息
SELECT 
    id,
    username,
    real_name,
    role_id,
    merchant_id,
    status,
    deleted
FROM sys_user
WHERE username = 'XHH' OR real_name LIKE '%灰灰%';

-- 2. 查询所有商户信息
SELECT 
    id AS merchant_id,
    admin_user_id,
    merchant_name,
    contact_person,
    contact_phone,
    audit_status
FROM merchant_info
WHERE deleted = 0;

-- 3. 查询小灰灰关联的商户详细信息
SELECT 
    u.id AS user_id,
    u.username,
    u.real_name,
    u.merchant_id,
    mi.merchant_name,
    mi.contact_person,
    mi.admin_user_id
FROM sys_user u
LEFT JOIN merchant_info mi ON u.merchant_id = mi.id
WHERE u.username = 'XHH' OR u.real_name LIKE '%灰灰%';

-- 4. 如果小灰灰的 merchant_id 不是 2，修正它
-- UPDATE sys_user SET merchant_id = 2 WHERE username = 'XHH';
