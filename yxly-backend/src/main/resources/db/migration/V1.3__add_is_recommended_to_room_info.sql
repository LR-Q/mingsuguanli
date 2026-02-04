-- 添加首页推荐字段（若已存在则跳过）
SET @col_is_recommended_exists := (
    SELECT COUNT(1) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'room_info' AND column_name = 'is_recommended'
);
SET @sql_add_is_recommended := IF(
    @col_is_recommended_exists = 0,
    'ALTER TABLE room_info ADD COLUMN is_recommended TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''首页推荐(0否,1是)'';',
    'SELECT 1;'
);
PREPARE stmt FROM @sql_add_is_recommended;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加推荐时间字段（若已存在则跳过）
SET @col_recommend_time_exists := (
    SELECT COUNT(1) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'room_info' AND column_name = 'recommend_time'
);
SET @sql_add_recommend_time := IF(
    @col_recommend_time_exists = 0,
    'ALTER TABLE room_info ADD COLUMN recommend_time DATETIME NULL COMMENT ''推荐时间'';',
    'SELECT 1;'
);
PREPARE stmt FROM @sql_add_recommend_time;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 索引，提升推荐查询性能（若已存在则跳过）
SET @idx_is_recommended_exists := (
    SELECT COUNT(1) FROM information_schema.statistics 
    WHERE table_schema = DATABASE() AND table_name = 'room_info' AND index_name = 'idx_room_is_recommended'
);
SET @sql_create_idx_is_recommended := IF(
    @idx_is_recommended_exists = 0,
    'CREATE INDEX idx_room_is_recommended ON room_info (is_recommended);',
    'SELECT 1;'
);
PREPARE stmt FROM @sql_create_idx_is_recommended;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_recommend_time_exists := (
    SELECT COUNT(1) FROM information_schema.statistics 
    WHERE table_schema = DATABASE() AND table_name = 'room_info' AND index_name = 'idx_room_recommend_time'
);
SET @sql_create_idx_recommend_time := IF(
    @idx_recommend_time_exists = 0,
    'CREATE INDEX idx_room_recommend_time ON room_info (recommend_time);',
    'SELECT 1;'
);
PREPARE stmt FROM @sql_create_idx_recommend_time;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
