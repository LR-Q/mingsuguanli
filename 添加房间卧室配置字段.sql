-- ===================================================================
-- 添加房间卧室配置字段
-- ===================================================================
-- 功能：支持多房型（如两房一厅、三房一厅）分别设置每个卧室的床型
-- ===================================================================

-- 1. 添加bedroom_config字段到room_info表
ALTER TABLE room_info ADD COLUMN bedroom_config JSON COMMENT '卧室配置(JSON格式，存储多个卧室的床型信息)' AFTER bed_type;

-- 2. 查看表结构
DESC room_info;

-- 3. 示例数据格式说明
/*
bedroom_config 字段存储格式示例：

单房（一房）:
{
  "bedrooms": [
    {
      "name": "卧室",
      "bedType": "大床",
      "bedCount": 1
    }
  ]
}

两房一厅:
{
  "bedrooms": [
    {
      "name": "主卧",
      "bedType": "大床",
      "bedCount": 1
    },
    {
      "name": "次卧",
      "bedType": "双床",
      "bedCount": 2
    }
  ]
}

三房一厅:
{
  "bedrooms": [
    {
      "name": "主卧",
      "bedType": "大床",
      "bedCount": 1
    },
    {
      "name": "次卧1",
      "bedType": "双床",
      "bedCount": 2
    },
    {
      "name": "次卧2",
      "bedType": "单人床",
      "bedCount": 1
    }
  ]
}
*/

-- 4. 为现有数据添加默认配置
-- 将现有的bed_type转换为bedroom_config格式
UPDATE room_info 
SET bedroom_config = JSON_OBJECT(
    'bedrooms', JSON_ARRAY(
        JSON_OBJECT(
            'name', '卧室',
            'bedType', COALESCE(bed_type, '大床'),
            'bedCount', 1
        )
    )
)
WHERE bedroom_config IS NULL AND deleted = 0;

-- 5. 验证数据
SELECT 
    id,
    room_number,
    bed_type AS old_bed_type,
    bedroom_config AS new_bedroom_config
FROM room_info 
WHERE deleted = 0
LIMIT 10;
