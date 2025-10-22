# 数据库脚本使用说明

## 📁 文件说明

### `create_merchant_tables.sql`
民宿主管理员注册功能的数据库表结构脚本。

**包含内容**：
- 扩展 sys_user 表（添加 merchant_id, merchant_status, apply_time）
- 创建 merchant_info 表（商户信息）
- 创建 merchant_apply_log 表（申请记录）
- 插入角色数据（SUPER_ADMIN, HOMESTAY_ADMIN, USER）
- 扩展 room_info 和 booking_order 表（添加 merchant_id）

---

## 🚀 执行方式

### 方式一：MySQL 客户端执行（推荐）

```bash
# 1. 打开 MySQL 客户端
mysql -u root -p

# 2. 选择数据库
USE yxly_dev;

# 3. 复制 create_merchant_tables.sql 的内容
# 4. 粘贴到 MySQL 客户端中执行
```

### 方式二：使用 Navicat 等工具

1. 打开 Navicat
2. 连接到数据库
3. 打开 `create_merchant_tables.sql` 文件
4. 点击"运行"按钮

### 方式三：使用 DBeaver

1. 打开 DBeaver
2. 连接到 yxly_dev 数据库
3. 新建 SQL 编辑器
4. 复制 create_merchant_tables.sql 内容
5. 执行脚本

---

## ✅ 验证执行结果

执行后运行以下 SQL 验证：

```sql
-- 检查 sys_user 表字段
SHOW COLUMNS FROM sys_user LIKE '%merchant%';

-- 检查商户表
SHOW TABLES LIKE 'merchant%';

-- 检查角色数据
SELECT id, role_name, role_code FROM sys_role;
```

**预期结果**：
- sys_user 有 merchant_id, merchant_status 字段
- 存在 merchant_info 和 merchant_apply_log 表
- 有 3 个角色：SUPER_ADMIN, HOMESTAY_ADMIN, USER

---

## ⚠️ 注意事项

1. **数据库名称**：确保使用的是 `yxly_dev` 数据库
2. **编码问题**：如果通过命令行执行出现编码错误，请使用 MySQL 客户端或图形工具
3. **已存在的字段**：脚本会尝试添加字段，如果字段已存在会报错，可忽略
4. **备份**：建议执行前备份数据库

---

## 📊 已执行状态

✅ **已成功执行**（2024-10-21）

如需重新执行，请先删除相关表：
```sql
DROP TABLE IF EXISTS merchant_info;
DROP TABLE IF EXISTS merchant_apply_log;
```
