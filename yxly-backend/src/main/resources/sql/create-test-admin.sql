-- Create a test admin with a known working BCrypt hash
-- Password: admin123
-- This hash is generated using online BCrypt generator

UPDATE sys_user 
SET password = '$2y$10$eImiTXuWVxfM37uY4JANjOFhyGgQpj8f8hjBUy1zCgUzuXoR.4B4.'
WHERE username = 'admin';
