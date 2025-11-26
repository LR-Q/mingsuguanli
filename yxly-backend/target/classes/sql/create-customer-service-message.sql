CREATE TABLE IF NOT EXISTS `customer_service_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `username` VARCHAR(64) DEFAULT NULL COMMENT '用户名',
  `user_phone` VARCHAR(32) DEFAULT NULL COMMENT '用户手机号',
  `room_id` BIGINT NOT NULL COMMENT '房间ID',
  `room_number` VARCHAR(50) DEFAULT NULL COMMENT '房间号',
  `room_type_name` VARCHAR(100) DEFAULT NULL COMMENT '房型名称',
  `location_id` BIGINT DEFAULT NULL COMMENT '民宿位置ID',
  `location_name` VARCHAR(100) DEFAULT NULL COMMENT '民宿位置名称',
  `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
  `merchant_name` VARCHAR(120) DEFAULT NULL COMMENT '商户名称',
  `admin_user_id` BIGINT DEFAULT NULL COMMENT '管理员用户ID',
  `content` TEXT COMMENT '留言内容',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0待处理 1处理中 2已完成',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_merchant` (`merchant_id`),
  KEY `idx_room` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服消息记录';

