-- Reset admin password to a simple one for testing
-- This uses a well-known BCrypt hash for "admin123"

UPDATE sys_user 
SET password = '$2a$10$DowJonesIndex.K9ULPhUhXu.O/j68LIH.F4OLYqsOyOeOK2iBNmDi'
WHERE username = 'admin';

-- Verify the update
SELECT username, LEFT(password, 30) as password_hash, status FROM sys_user WHERE username = 'admin';
