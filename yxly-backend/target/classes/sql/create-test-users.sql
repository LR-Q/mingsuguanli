-- 创建10个测试用户
-- 所有用户密码统一为: test123 (BCrypt加密)

INSERT INTO sys_user (
    username, 
    password, 
    real_name, 
    email, 
    phone, 
    role_id, 
    status, 
    email_verified, 
    phone_verified, 
    create_time, 
    update_time, 
    deleted
) VALUES 
-- 用户1: 张三
('zhangsan', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '张三', 'zhangsan@example.com', '13800001001', 2, 1, 1, 1, NOW(), NOW(), 0),

-- 用户2: 李四
('lisi', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '李四', 'lisi@example.com', '13800001002', 2, 1, 0, 1, NOW(), NOW(), 0),

-- 用户3: 王五
('wangwu', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '王五', 'wangwu@example.com', '13800001003', 2, 1, 1, 0, NOW(), NOW(), 0),

-- 用户4: 赵六
('zhaoliu', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '赵六', 'zhaoliu@example.com', '13800001004', 2, 0, 0, 0, NOW(), NOW(), 0),

-- 用户5: 孙七
('sunqi', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '孙七', 'sunqi@example.com', '13800001005', 2, 1, 1, 1, NOW(), NOW(), 0),

-- 用户6: 周八
('zhouba', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '周八', 'zhouba@example.com', '13800001006', 2, 1, 0, 0, NOW(), NOW(), 0),

-- 用户7: 吴九
('wujiu', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '吴九', 'wujiu@example.com', '13800001007', 2, 1, 1, 0, NOW(), NOW(), 0),

-- 用户8: 郑十
('zhengshi', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '郑十', 'zhengshi@example.com', '13800001008', 2, 1, 0, 1, NOW(), NOW(), 0),

-- 用户9: 陈小明
('chenxiaoming', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '陈小明', 'chenxm@example.com', '13800001009', 2, 1, 1, 1, NOW(), NOW(), 0),

-- 用户10: 刘小红
('liuxiaohong', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', '刘小红', 'liuxh@example.com', '13800001010', 2, 1, 1, 1, NOW(), NOW(), 0);

-- 更新一些用户的最后登录时间（模拟不同的登录活跃度）
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 1 DAY), last_login_ip = '192.168.1.100' WHERE username = 'zhangsan';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 3 DAY), last_login_ip = '192.168.1.101' WHERE username = 'lisi';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 7 DAY), last_login_ip = '192.168.1.102' WHERE username = 'wangwu';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 2 HOUR), last_login_ip = '192.168.1.103' WHERE username = 'sunqi';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 5 DAY), last_login_ip = '192.168.1.104' WHERE username = 'zhouba';

-- 验证插入结果
SELECT COUNT(*) as '新增用户数量' FROM sys_user WHERE username != 'admin';
SELECT username, real_name, email, phone, status FROM sys_user WHERE username != 'admin' ORDER BY create_time DESC;
