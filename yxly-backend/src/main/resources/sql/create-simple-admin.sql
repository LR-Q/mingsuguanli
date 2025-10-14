-- Delete existing admin and create a new one
DELETE FROM sys_user WHERE username = 'admin';

-- Insert new admin with a simple password hash
-- Password: admin123
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
    '$2a$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.', 
    'Administrator', 
    'admin@example.com', 
    '13800000000', 
    1, 
    1, 
    1, 
    1, 
    NOW(), 
    NOW(), 
    0
);
