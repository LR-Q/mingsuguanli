-- =============================================
-- 为 location_info 表添加 merchant_id 字段
-- 实现位置的权限隔离
-- =============================================

USE yxly_dev;

-- 1. 添加 merchant_id 字段
ALTER TABLE location_info
ADD COLUMN merchant_id BIGINT DEFAULT NULL COMMENT '商户ID' AFTER id;

-- 2. 添加索引
CREATE INDEX idx_merchant_id ON location_info(merchant_id);

-- 3. 为现有位置关联商户（如果存在的话）
-- 这里将现有位置关联到第一个商户（如果有的话）
UPDATE location_info loc
SET loc.merchant_id = (
    SELECT MIN(id) FROM merchant_info WHERE deleted = 0 AND audit_status = 1
)
WHERE loc.merchant_id IS NULL AND loc.deleted = 0;

-- 4. 查看结果
SELECT 
    loc.id,
    loc.merchant_id,
    loc.name,
    loc.address,
    mi.merchant_name,
    mi.contact_person
FROM location_info loc
LEFT JOIN merchant_info mi ON loc.merchant_id = mi.id
WHERE loc.deleted = 0
ORDER BY loc.id;

SELECT '✓ location_info 表已成功添加 merchant_id 字段' AS result;
