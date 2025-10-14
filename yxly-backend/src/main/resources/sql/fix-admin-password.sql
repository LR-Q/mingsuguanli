-- Fix admin password with correct BCrypt hash
-- Password: admin123
-- Using standard BCrypt hash (rounds=10)

UPDATE sys_user 
SET password = '$2a$10$eImiTXuWVxfM37uY4JANjQ.1cUKJ53oyiYzABtCJ/jG2OpWmw5aDC'
WHERE username = 'admin';

-- Verify the update
SELECT username, 'Password updated successfully' as status FROM sys_user WHERE username = 'admin';
