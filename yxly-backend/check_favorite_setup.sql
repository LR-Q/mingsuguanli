-- ============================================
-- 检查收藏功能配置
-- ============================================

USE yxly_db;

-- 1. 检查表是否存在
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ user_favorite 表已存在'
        ELSE '❌ user_favorite 表不存在，请先运行 create_favorite_table.sql'
    END AS table_check
FROM information_schema.tables 
WHERE table_schema = 'yxly_db' AND table_name = 'user_favorite';

-- 2. 检查表结构
DESC user_favorite;

-- 3. 检查索引
SHOW INDEX FROM user_favorite;

-- 4. 检查是否有数据
SELECT COUNT(*) AS total_favorites FROM user_favorite;

-- 5. 检查 room_info 表（收藏功能依赖）
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('✅ room_info 表存在，共 ', COUNT(*), ' 个房间')
        ELSE '❌ room_info 表不存在或没有数据'
    END AS room_check
FROM room_info WHERE deleted = 0;

-- 6. 检查 sys_user 表（收藏功能依赖）
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('✅ sys_user 表存在，共 ', COUNT(*), ' 个用户')
        ELSE '❌ sys_user 表不存在或没有数据'
    END AS user_check
FROM sys_user WHERE deleted = 0;

SELECT '========== 检查完成 ==========' AS message;
