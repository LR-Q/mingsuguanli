-- 位置信息表
CREATE TABLE IF NOT EXISTS `location_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '位置ID',
  `name` VARCHAR(100) NOT NULL COMMENT '位置名称',
  `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
  `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
  `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
  `longitude` DECIMAL(10, 7) NOT NULL COMMENT '经度',
  `latitude` DECIMAL(10, 7) NOT NULL COMMENT '纬度',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `description` TEXT COMMENT '位置描述',
  `map_type` VARCHAR(20) DEFAULT 'baidu' COMMENT '地图类型(baidu/amap/google)',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用(0:禁用 1:启用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置信息表';

-- 插入默认数据（示例）
INSERT INTO `location_info` (`name`, `address`, `province`, `city`, `district`, `longitude`, `latitude`, `contact_phone`, `description`, `is_active`) 
VALUES ('悦鑫乐怡民宿', '待设置', '待设置', '待设置', '待设置', 116.4073963, 39.9041999, '13800138000', '请在管理端设置民宿位置', 1);
