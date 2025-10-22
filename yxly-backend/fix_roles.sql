USE yxly_dev;

-- 清理并重建角色表（按照你的定义）
-- 管理员role_id=1, 用户role_id=2, 超级管理员role_id=3

-- 1. 更新角色表
UPDATE sys_role SET 
    role_name = '管理员（民宿主）',
    role_code = 'HOMESTAY_ADMIN',
    description = 'Homestay admin',
    permissions = '["room:*", "order:*", "customer:*", "finance:view"]'
WHERE id = 1;

UPDATE sys_role SET 
    role_name = '用户',
    role_code = 'USER',
    description = 'Normal user',
    permissions = '["room:view", "order:create", "order:view"]'
WHERE id = 2;

UPDATE sys_role SET 
    role_name = '超级管理员',
    role_code = 'SUPER_ADMIN',
    description = 'Platform super admin',
    permissions = '["*:*"]'
WHERE id = 3;

-- 2. 删除多余的角色（如果id=4存在）
DELETE FROM sys_role WHERE id = 4;

-- 3. 验证角色配置
SELECT 
    id as role_id,
    role_code,
    role_name,
    description
FROM sys_role
WHERE deleted = 0
ORDER BY id;
