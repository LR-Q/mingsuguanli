USE yxly_dev;

INSERT INTO sys_user (username, password, real_name, email, phone, role_id, status, email_verified, phone_verified, balance, create_time, update_time, deleted)
VALUES ('superadmin', '$2a$10$5Z8FvBLOF4KqVYj8nHYOLu0R3jVx7xqK0h.JF2nYpQGRiN9QE.W5K', '超级管理员', 'super@admin.com', '13900000000', (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN'), 1, 1, 1, 0.00, NOW(), NOW(), 0);

SELECT u.id, u.username, u.real_name, r.role_name, r.role_code FROM sys_user u LEFT JOIN sys_role r ON u.role_id = r.id WHERE u.username IN ('superadmin', 'admin') AND u.deleted = 0;
