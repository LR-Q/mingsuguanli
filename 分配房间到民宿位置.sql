-- ===================================================================
-- 分配房间到民宿位置 (location_info)
-- ===================================================================
-- 
-- 说明：
-- 1. merchant_info 表 = 商户资质表（一个管理员一条记录）
-- 2. location_info 表 = 民宿位置表（一个商户可以有多个民宿位置）
-- 3. room_info 表 = 房间表（一个民宿位置有多个房间）
-- 
-- 数据关系：
-- SysUser (XHH, id=29) 
--   → merchant_info (商户资质, id=1, admin_user_id=29)
--     → location_info (小饭灰1号, id=1, merchant_id=1)
--     → location_info (小饭灰2号, id=2, merchant_id=1)
--       → room_info (房间, location_id=1 或 2)
-- ===================================================================

-- ========== 步骤1：查看当前数据 ==========

-- 查看民宿位置
SELECT id, merchant_id, name, address FROM location_info WHERE deleted = 0;

-- 查看房间
SELECT id, location_id, room_number, floor_number FROM room_info WHERE deleted = 0;

-- ========== 步骤2：分配房间到民宿位置 ==========

-- 方案A：假设 location_info 表中 id=1 是"小饭灰1号"，id=2 是"小饭灰2号"

-- 将 110, 120, 121 分配给"小饭灰1号" (location_id=1)
UPDATE room_info 
SET location_id = 1 
WHERE room_number IN ('110', '120', '121') AND deleted = 0;

-- 将 210, 232, 330 分配给"小饭灰2号" (location_id=2)
UPDATE room_info 
SET location_id = 2 
WHERE room_number IN ('210', '232', '330') AND deleted = 0;


-- ========== 方案B：如果需要先创建"小饭灰2号"位置 ==========
-- （如果 location_info 表中只有一个位置，需要先创建第二个）

INSERT INTO location_info (
    merchant_id,
    name,
    address,
    province,
    city,
    district,
    longitude,
    latitude,
    contact_phone,
    description,
    map_type,
    is_active,
    create_time,
    update_time,
    deleted
) VALUES (
    1,                                          -- merchant_id (XHH的商户ID)
    '小饭灰2号',                                 -- 民宿名称
    '广东省梅州市梅县区大埔路X街道',              -- 详细地址
    '广东省',                                   -- 省份
    '梅州市',                                   -- 城市
    '梅县区',                                   -- 区县
    116.0802237,                                -- 经度
    24.2905623,                                 -- 纬度
    '19772652172',                              -- 联系电话
    '小饭灰的第二个民宿位置',                     -- 描述
    'baidu',                                    -- 地图类型
    1,                                          -- 启用状态
    NOW(),                                      -- 创建时间
    NOW(),                                      -- 更新时间
    0                                           -- 未删除
);

-- 然后再分配房间（使用刚才插入的 location_id）
-- 假设新插入的记录 id 是 2

UPDATE room_info 
SET location_id = 2 
WHERE room_number IN ('210', '232', '330') AND deleted = 0;


-- ========== 步骤3：验证分配结果 ==========

SELECT 
    r.id AS room_id,
    r.room_number,
    r.floor_number,
    r.location_id,
    l.name AS location_name,
    l.merchant_id,
    m.merchant_name AS merchant_name
FROM room_info r
LEFT JOIN location_info l ON r.location_id = l.id
LEFT JOIN merchant_info m ON l.merchant_id = m.id
WHERE r.deleted = 0
ORDER BY r.room_number;


-- ========== 步骤4：查看 XHH 可以看到的民宿位置 ==========

-- 根据 admin_user_id=29 找到商户
SELECT id, merchant_name FROM merchant_info WHERE admin_user_id = 29 AND deleted = 0;

-- 根据 merchant_id 找到所有民宿位置
SELECT id, name, address 
FROM location_info 
WHERE merchant_id IN (
    SELECT id FROM merchant_info WHERE admin_user_id = 29 AND deleted = 0
)
AND is_active = 1 
AND deleted = 0;


-- ========== 清理脚本（如果需要重新分配） ==========

-- 清空所有房间的 location_id
-- UPDATE room_info SET location_id = NULL WHERE deleted = 0;
