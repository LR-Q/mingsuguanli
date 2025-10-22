-- ============================================
-- 创建用户收藏表
-- 执行方式：在MySQL中直接执行此脚本
-- ============================================

USE yxly_db;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `user_favorite`;

-- 创建用户收藏表
CREATE TABLE `user_favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `room_id` BIGINT(20) NOT NULL COMMENT '房间ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常 1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_room` (`user_id`, `room_id`, `deleted`) COMMENT '用户-房间-删除标记唯一索引',
    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
    KEY `idx_room_id` (`room_id`) COMMENT '房间ID索引',
    KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 查看表结构
DESC user_favorite;

-- 显示创建表的SQL
SHOW CREATE TABLE user_favorite;

SELECT '✅ 用户收藏表创建成功！' AS message;
