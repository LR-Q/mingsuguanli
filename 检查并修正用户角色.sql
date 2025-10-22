USE yxly_dev;

-- 查看所有关键用户的角色
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
WHERE u.username IN ('admin', 'superadmin', 'LLL') 
  AND u.deleted = 0
ORDER BY u.username;

-- 如果admin不是HOMESTAY_ADMIN（role_id=1），执行以下更新
-- UPDATE sys_user SET role_id = 1 WHERE username = 'admin' AND deleted = 0;

SELECT '
==========================================
角色说明:
  role_id=1: HOMESTAY_ADMIN - 管理员（跳转到/admin）
  role_id=2: USER - 普通用户（跳转到/home）
  role_id=3: SUPER_ADMIN - 超级管理员（跳转到/super-admin/merchants）

建议配置:
  admin → role_id=1 (HOMESTAY_ADMIN)
  superadmin → role_id=3 (SUPER_ADMIN)
  其他用户 → role_id=2 (USER)
==========================================
' as info;
