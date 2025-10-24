-- 为 room_info 表添加 location_id 字段，关联民宿位置
ALTER TABLE `room_info` 
ADD COLUMN `location_id` BIGINT(20) NULL COMMENT '民宿位置ID（关联location_info表）' AFTER `id`,
ADD INDEX `idx_location_id` (`location_id`);

-- 添加外键约束（可选，根据需要）
-- ALTER TABLE `room_info` 
-- ADD CONSTRAINT `fk_room_location` 
-- FOREIGN KEY (`location_id`) REFERENCES `location_info` (`id`) ON DELETE RESTRICT;

-- 更新现有数据：将现有房间关联到第一个民宿位置（临时方案）
-- 注意：生产环境中需要根据实际情况手动分配
UPDATE `room_info` r
SET r.`location_id` = (SELECT MIN(id) FROM `location_info` WHERE deleted = 0 AND is_active = 1)
WHERE r.`location_id` IS NULL AND EXISTS (SELECT 1 FROM `location_info` WHERE deleted = 0 AND is_active = 1);
