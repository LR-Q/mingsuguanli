-- 添加 location_id 列（若已存在则跳过）
SET @col_location_id_exists := (
    SELECT COUNT(1) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'room_info' AND column_name = 'location_id'
);
SET @sql_add_location_id := IF(
    @col_location_id_exists = 0,
    'ALTER TABLE `room_info` ADD COLUMN `location_id` BIGINT(20) NULL COMMENT ''民宿位置ID（关联location_info表）'' AFTER `id`;',
    'SELECT 1;'
);
PREPARE stmt FROM @sql_add_location_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为 location_id 创建索引（若已存在则跳过）
SET @idx_location_id_exists := (
    SELECT COUNT(1) FROM information_schema.statistics 
    WHERE table_schema = DATABASE() AND table_name = 'room_info' AND index_name = 'idx_location_id'
);
SET @sql_create_idx_location_id := IF(
    @idx_location_id_exists = 0,
    'CREATE INDEX idx_location_id ON room_info (location_id);',
    'SELECT 1;'
);
PREPARE stmt FROM @sql_create_idx_location_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加外键约束（可选，根据需要）
-- ALTER TABLE `room_info` 
-- ADD CONSTRAINT `fk_room_location` 
-- FOREIGN KEY (`location_id`) REFERENCES `location_info` (`id`) ON DELETE RESTRICT;

-- 更新现有数据：将现有房间关联到第一个民宿位置（临时方案）
-- 注意：生产环境中需要根据实际情况手动分配
UPDATE `room_info` r
SET r.`location_id` = (SELECT MIN(id) FROM `location_info` WHERE deleted = 0 AND is_active = 1)
WHERE r.`location_id` IS NULL AND EXISTS (SELECT 1 FROM `location_info` WHERE deleted = 0 AND is_active = 1);
