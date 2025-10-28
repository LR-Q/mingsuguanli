-- 房间类型表和房间信息表创建脚本
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

CREATE TABLE IF NOT EXISTS room_info (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    room_number      VARCHAR(20)   NOT NULL COMMENT '房间号',
    room_type_id     BIGINT        NOT NULL COMMENT '房型ID',
    floor_number     INT           NULL COMMENT '楼层',
    area             DECIMAL(5,2)  NULL COMMENT '面积(平方米)',
    bed_type         VARCHAR(50)   NULL COMMENT '床型',
    max_guests       INT           NOT NULL DEFAULT 2 COMMENT '最大入住人数',
    base_price       DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    current_price    DECIMAL(10,2) NOT NULL COMMENT '当前价格',
    status           TINYINT       NOT NULL DEFAULT 1 COMMENT '房间状态(1:可用 2:占用 3:维修 4:清洁 5:停用)',
    facilities       JSON          NULL COMMENT '房间设施',
    description      TEXT          NULL COMMENT '房间描述',
    images           JSON          NULL COMMENT '房间图片',
    wifi_password    VARCHAR(50)   NULL COMMENT 'WiFi 密码',
    last_clean_time  DATETIME      NULL COMMENT '最后清洁时间',
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_number (room_number),
    KEY idx_room_type_id (room_type_id),
    KEY idx_status (status),
    KEY idx_floor_number (floor_number),
    CONSTRAINT fk_room_type FOREIGN KEY (room_type_id) REFERENCES room_type (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间信息表';

-- 插入基础房型数据
INSERT INTO room_type (type_name, type_code, description, base_price, max_guests, bed_type, area, facilities, sort_order, status) VALUES
('标准间', 'standard', '标准双人间，配备基础设施', 288.00, 2, 'double', 25.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器"]', 1, 1),
('大床房', 'king', '豪华大床房，宽敞舒适', 358.00, 2, 'king', 30.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器", "冰箱", "吹风机"]', 2, 1),
('双床房', 'twin', '双床房，适合朋友出行', 328.00, 2, 'twin', 28.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器", "吹风机"]', 3, 1),
('豪华套房', 'suite', '豪华套房，配备客厅和阳台', 588.00, 4, 'king', 50.00, '["空调", "电视", "WiFi", "独立卫浴", "热水器", "冰箱", "洗衣机", "微波炉", "吹风机", "阳台"]', 4, 1);

-- 插入示例房间数据
INSERT INTO room_info (room_number, room_type_id, floor_number, area, bed_type, max_guests, base_price, current_price, status, facilities, description, wifi_password) VALUES
('101', 1, 1, 25.00, 'double', 2, 288.00, 288.00, 1, '["空调", "电视", "WiFi", "独立卫浴"]', '标准双人间，配备独立卫浴', 'room101wifi'),
('102', 2, 1, 30.00, 'king', 2, 358.00, 358.00, 2, '["空调", "电视", "WiFi", "独立卫浴", "冰箱"]', '豪华大床房，景观优美', 'room102wifi'),
('201', 4, 2, 50.00, 'king', 4, 588.00, 588.00, 1, '["空调", "电视", "WiFi", "独立卫浴", "冰箱", "阳台"]', '豪华套房，配备客厅和阳台', 'room201wifi'),
('202', 3, 2, 28.00, 'twin', 2, 328.00, 328.00, 3, '["空调", "电视", "WiFi", "独立卫浴"]', '双床房，适合朋友出行', 'room202wifi');










