-- 创建用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `room_id` BIGINT(20) NOT NULL COMMENT '房间ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常 1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_room` (`user_id`, `room_id`, `deleted`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';
