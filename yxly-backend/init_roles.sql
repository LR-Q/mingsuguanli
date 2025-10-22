USE yxly_dev;

-- 重新创建角色数据
-- role_id=1: 管理员（民宿主）
-- role_id=2: 用户
-- role_id=3: 超级管理员

-- 清空角色表（如果有数据）
DELETE FROM sys_role;

-- 插入三个角色
INSERT INTO sys_role (id, role_name, role_code, description, permissions, create_time, update_time, deleted) VALUES
(1, '管理员（民宿主）', 'HOMESTAY_ADMIN', 'Homestay admin', '["room:*", "order:*", "customer:*", "finance:view"]', NOW(), NOW(), 0),
(2, '用户', 'USER', 'Normal user', '["room:view", "order:create", "order:view"]', NOW(), NOW(), 0),
(3, '超级管理员', 'SUPER_ADMIN', 'Platform super admin', '["*:*"]', NOW(), NOW(), 0);

-- 验证角色创建结果
SELECT 
    id as role_id,
    role_name,
    role_code,
    description
FROM sys_role
ORDER BY id;
