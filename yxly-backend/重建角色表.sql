USE yxly_dev;

-- 重建角色表数据（处理外键约束）

-- 方法：临时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空角色表
DELETE FROM sys_role;

-- 重置自增ID（确保从1开始）
ALTER TABLE sys_role AUTO_INCREMENT = 1;

-- 插入三个角色
INSERT INTO sys_role (id, role_name, role_code, description, permissions, create_time, update_time, deleted) VALUES
(1, '管理员（民宿主）', 'HOMESTAY_ADMIN', 'Homestay admin', '["room:*", "order:*", "customer:*", "finance:view"]', NOW(), NOW(), 0),
(2, '用户', 'USER', 'Normal user', '["room:view", "order:create", "order:view"]', NOW(), NOW(), 0),
(3, '超级管理员', 'SUPER_ADMIN', 'Platform super admin', '["*:*"]', NOW(), NOW(), 0);

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 验证角色创建结果
SELECT 
    id as role_id,
    role_name,
    role_code,
    description
FROM sys_role
ORDER BY id;

SELECT '角色表重建完成！' as status;
