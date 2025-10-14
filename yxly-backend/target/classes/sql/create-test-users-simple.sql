-- Create 10 test users
-- All users password: test123 (BCrypt encoded)

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
('user001', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Zhang San', 'user001@example.com', '13800001001', 2, 1, 1, 1, NOW(), NOW(), 0),
('user002', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Li Si', 'user002@example.com', '13800001002', 2, 1, 0, 1, NOW(), NOW(), 0),
('user003', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Wang Wu', 'user003@example.com', '13800001003', 2, 1, 1, 0, NOW(), NOW(), 0),
('user004', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Zhao Liu', 'user004@example.com', '13800001004', 2, 0, 0, 0, NOW(), NOW(), 0),
('user005', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Sun Qi', 'user005@example.com', '13800001005', 2, 1, 1, 1, NOW(), NOW(), 0),
('user006', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Zhou Ba', 'user006@example.com', '13800001006', 2, 1, 0, 0, NOW(), NOW(), 0),
('user007', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Wu Jiu', 'user007@example.com', '13800001007', 2, 1, 1, 0, NOW(), NOW(), 0),
('user008', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Zheng Shi', 'user008@example.com', '13800001008', 2, 1, 0, 1, NOW(), NOW(), 0),
('user009', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Chen Ming', 'user009@example.com', '13800001009', 2, 1, 1, 1, NOW(), NOW(), 0),
('user010', '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 'Liu Hong', 'user010@example.com', '13800001010', 2, 1, 1, 1, NOW(), NOW(), 0);

-- Update some users last login time
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 1 DAY), last_login_ip = '192.168.1.100' WHERE username = 'user001';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 3 DAY), last_login_ip = '192.168.1.101' WHERE username = 'user002';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 7 DAY), last_login_ip = '192.168.1.102' WHERE username = 'user003';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 2 HOUR), last_login_ip = '192.168.1.103' WHERE username = 'user005';
UPDATE sys_user SET last_login_time = DATE_SUB(NOW(), INTERVAL 5 DAY), last_login_ip = '192.168.1.104' WHERE username = 'user006';
