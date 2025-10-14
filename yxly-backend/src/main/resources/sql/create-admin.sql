-- Create admin user
-- Password: admin123 (BCrypt encoded)

-- Insert admin role
INSERT INTO sys_role (role_name, role_code, description, status, create_time, update_time, deleted) 
VALUES ('Super Admin', 'ADMIN', 'System super administrator with all permissions', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- Insert admin user
-- Password admin123 BCrypt hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIh9TcOKYHKu6P5wHKxNKYHNka
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
) VALUES (
    'admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIh9TcOKYHKu6P5wHKxNKYHNka', 
    'System Administrator', 
    'admin@yxly.com', 
    '13800138000', 
    1, 
    1, 
    1, 
    1, 
    NOW(), 
    NOW(), 
    0
) ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    real_name = VALUES(real_name),
    email = VALUES(email),
    phone = VALUES(phone),
    update_time = NOW();
