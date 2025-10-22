# 位置 merchant_id 错误问题修复方案

## 问题描述
小灰灰用户的 `merchant_id=2`，但创建的位置显示 `merchant_id=1`。

## 根本原因

### 1. 登录时未返回 merchantId
之前的 `LoginResponse.UserInfo` 没有包含 `merchantId` 字段，导致：
- 前端无法获取用户的商户ID
- JWT token 解析后也无法得到商户信息
- 虽然后端从数据库实时查询用户，但某些逻辑可能使用了错误的值

### 2. 用户可能在设置 merchant_id 之前登录
- 如果用户在 `merchant_id` 被设置之前登录
- JWT token 中的用户快照就是旧的数据
- 需要重新登录才能刷新

## 已修复内容

### 后端修改

#### 1. LoginResponse.UserInfo 添加 merchantId 字段
**文件**: `LoginResponse.java`
```java
@Schema(description = "商户ID")
private Long merchantId;
```

#### 2. 登录时返回 merchantId
**文件**: `AuthServiceImpl.java`
```java
LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
    .id(user.getId())
    .username(user.getUsername())
    .realName(user.getRealName())
    .email(user.getEmail())
    .phone(user.getPhone())
    .avatar(user.getAvatar())
    .roleId(user.getRoleId())
    .roleCode(roleCode)
    .merchantId(user.getMerchantId())  // ✅ 添加商户ID
    .lastLoginTime(user.getLastLoginTime())
    .build();
```

#### 3. 增强权限验证
**文件**: `LocationInfoServiceImpl.java`
- 添加了 null 检查
- 区分用户没有 merchantId 和位置没有 merchantId 的情况
- 提供更清晰的错误提示

## 解决步骤

### 步骤1: 确保数据库中用户的 merchant_id 正确

执行以下SQL检查：

```sql
-- 查看所有用户的商户关联
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.merchant_id,
    mi.merchant_name
FROM sys_user u
LEFT JOIN merchant_info mi ON u.merchant_id = mi.id
WHERE u.deleted = 0;

-- 如果发现用户的 merchant_id 不正确，执行关联
UPDATE sys_user u
INNER JOIN merchant_info mi ON u.id = mi.admin_user_id
SET u.merchant_id = mi.id
WHERE u.deleted = 0 AND mi.deleted = 0;
```

### 步骤2: 重启后端服务
代码已修改，需要重启生效。

### 步骤3: 所有用户重新登录
**重要**: 已登录的用户必须退出并重新登录，才能获取到正确的 `merchantId`。

### 步骤4: 修复已创建的错误位置

如果某些位置的 `merchant_id` 不正确，执行以下SQL修复：

```sql
-- 示例：将位置ID=2的merchant_id改为2（小灰灰的商户）
UPDATE location_info 
SET merchant_id = 2 
WHERE id = 2 AND deleted = 0;

-- 或者批量修复：根据位置名称推断归属
-- 这需要根据实际情况编写
```

## 测试验证

### 1. 查看登录响应
登录后，检查返回的 userInfo 是否包含 merchantId：
```json
{
  "code": 200,
  "data": {
    "userInfo": {
      "id": 29,
      "username": "XHH",
      "merchantId": 2,  // ✅ 应该有这个字段
      "roleId": 1,
      "roleCode": "SUPER_ADMIN"
    }
  }
}
```

### 2. 创建位置测试
- 小灰灰登录后创建位置
- 检查数据库 `location_info` 表的 `merchant_id` 字段
- 应该等于 2（小灰灰的 merchant_id）

### 3. 权限验证测试
- 用户A创建位置后，用户B无法编辑
- 应该收到 "无权修改此位置信息" 的错误提示

## 预期结果

✅ 登录时返回正确的 merchantId  
✅ 创建位置时自动关联当前用户的 merchantId  
✅ 不同商户的管理员无法编辑对方的位置  
✅ 超级管理员可以管理所有位置  

## 注意事项

1. **必须重新登录**: 已登录用户的 token 中没有 merchantId，必须重新登录
2. **检查数据库数据**: 确保 sys_user 表的 merchant_id 字段已正确设置
3. **清理错误数据**: 之前创建的位置可能需要手动修正 merchant_id
