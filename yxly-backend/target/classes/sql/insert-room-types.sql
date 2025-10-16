-- 插入房型数据
-- 如果表不存在则创建
CREATE TABLE IF NOT EXISTS room_type (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '房型ID',
    type_name       VARCHAR(50)  NOT NULL COMMENT '房型名称',
    type_code       VARCHAR(20)  NOT NULL COMMENT '房型编码',
    description     TEXT         NULL COMMENT '房型描述',
    base_price      DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    max_guests      INT          NOT NULL DEFAULT 2 COMMENT '最大入住人数',
    bed_type        VARCHAR(50)  NULL COMMENT '床型',
    area            DECIMAL(5,2) NULL COMMENT '面积(平方米)',
    facilities      JSON         NULL COMMENT '标准设施',
    images          JSON         NULL COMMENT '房型图片',
    sort_order      INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_code (type_code),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间类型表';

-- 清空现有数据（如果有的话）
DELETE FROM room_type WHERE 1=1;

-- 插入房型数据（房型指户型布局，如几房几厅）
INSERT INTO room_type (type_name, type_code, description, base_price, max_guests, bed_type, area, facilities, sort_order, status) VALUES
('单人房', 'single_room', '单人房，适合单人入住', 188.00, 1, '单人床', 15.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器"]', 1, 1),
('标准间', 'standard_room', '标准间，一房无厅，配备基础设施', 288.00, 2, '双人床', 25.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器"]', 2, 1),
('一房一厅', 'one_bed_one_living', '一房一厅，独立客厅和卧室', 388.00, 3, '大床', 35.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器", "冰箱", "沙发"]', 3, 1),
('两房一厅', 'two_bed_one_living', '两房一厅，适合家庭或朋友出行', 588.00, 4, '双床', 50.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器", "冰箱", "洗衣机", "沙发"]', 4, 1),
('两房两厅', 'two_bed_two_living', '两房两厅，宽敞舒适', 688.00, 5, '双人床', 65.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器", "冰箱", "洗衣机", "沙发", "餐桌"]', 5, 1),
('三房两厅', 'three_bed_two_living', '三房两厅，适合大家庭', 888.00, 6, '上下铺', 80.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器", "冰箱", "洗衣机", "沙发", "餐桌", "书桌"]', 6, 1);
