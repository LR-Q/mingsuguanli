-- 房间评论表
CREATE TABLE IF NOT EXISTS `room_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` BIGINT NOT NULL COMMENT '房间ID',
  `order_id` BIGINT NULL COMMENT '订单ID（可为空）',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `rating` TINYINT NOT NULL COMMENT '评分 1-5',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `anonymous` TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名(0否 1是)',
  `is_append` TINYINT NOT NULL DEFAULT 0 COMMENT '是否追评(0否 1是)',
  `parent_id` BIGINT NULL COMMENT '追评关联首评ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0正常 1删除)',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  UNIQUE KEY `uk_order_main` (`order_id`,`is_append`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间评论表';


